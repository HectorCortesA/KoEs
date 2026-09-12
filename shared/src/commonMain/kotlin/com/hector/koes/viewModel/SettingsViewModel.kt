package com.hector.koes.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hector.koes.database.DatabaseModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class WritingMode {
    PALABRAS, ORACIONES
}

class SettingsViewModel : ViewModel() {
    private val repository = DatabaseModule.repository

    private val _writingMode = MutableStateFlow(WritingMode.PALABRAS)
    val writingMode: StateFlow<WritingMode> = _writingMode

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _categories = MutableStateFlow<List<String>>(listOf("Todas"))
    val categories: StateFlow<List<String>> = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val dbCategories = repository.getCategories()
                // Limpiamos y ordenamos las categorías
                val uniqueCategories = dbCategories.map { it.trim() }.filter { it.isNotEmpty() }.distinct().sorted()
                _categories.value = listOf("Todas") + uniqueCategories
            } catch (e: Exception) {
                println("SettingsViewModel: Error cargando categorías: ${e.message}")
            }
        }
    }

    fun setWritingMode(mode: WritingMode) {
        if (_writingMode.value != mode) {
            _writingMode.value = mode
        }
    }

    fun setCategory(category: String) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
        }
    }
}

object SettingsManager {
    val instance: SettingsViewModel by lazy { SettingsViewModel() }
}
