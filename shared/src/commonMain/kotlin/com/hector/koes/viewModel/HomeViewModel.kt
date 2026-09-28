package com.hector.koes.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hector.koes.database.DatabaseModule
import com.hector.koes.model.FullDictionaryItem
import com.hector.koes.util.HangulUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository by lazy { DatabaseModule.repository }
    private val settings = SettingsManager.instance

    private val _currentItem = MutableStateFlow<FullDictionaryItem?>(null)
    val currentItem: StateFlow<FullDictionaryItem?> = _currentItem

    val writingMode: StateFlow<WritingMode> = settings.writingMode

    init {
        // Observamos los cambios en la configuración (Categoría y Modo)
        // para reaccionar y cargar un nuevo ítem aleatorio
        viewModelScope.launch {
            combine(settings.selectedCategory, settings.writingMode) { category, mode ->
                category to mode
            }
            .distinctUntilChanged()
            .collect { (category, mode) ->
                println("HomeViewModel: Detectado cambio en configuración -> Cat: $category, Modo: $mode")
                loadRandomItem()
            }
        }
    }

    fun loadRandomItem() {
        viewModelScope.launch {
            try {
                val category = settings.selectedCategory.value
                
                val item = if (category == "Todas") {
                    repository.getRandomAll()
                } else {
                    repository.getRandomByCategory(category)
                }
                
                if (item == null) {
                    println("HomeViewModel: No se encontró ningún ítem para la categoría: $category")
                } else {
                    println("HomeViewModel: Nuevo ítem cargado: ${item.wordSpanish} (${item.categoria})")
                }
                
                _currentItem.value = item
            } catch (e: Exception) {
                println("HomeViewModel: Error cargando ítem aleatorio: ${e.message}")
            }
        }
    }

    private var isTransitioning = false

    fun checkCompletion(input: String, onComplete: () -> Unit = {}) {
        if (isTransitioning) return
        val current = _currentItem.value ?: return
        val target = if (writingMode.value == WritingMode.PALABRAS) {
            current.wordCoreano
        } else {
            current.ejemploKoreano
        }

        val trimmedInput = input.trim()
        val trimmedTarget = target.trim()

        if (trimmedTarget.isNotEmpty() &&
            (trimmedInput == trimmedTarget || HangulUtils.decomposeToJamos(trimmedInput) == HangulUtils.decomposeToJamos(trimmedTarget))
        ) {
            println("HomeViewModel: ¡Palabra completada! Registrando progreso...")
            UserProgressManager.recordWordCompleted(trimmedTarget)
            isTransitioning = true
            onComplete()
            viewModelScope.launch {
                kotlinx.coroutines.delay(150)
                loadRandomItem()
                isTransitioning = false
            }
        }
    }
}
