package com.hector.koes.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hector.koes.database.DatabaseModule
import com.hector.koes.model.DictionaryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DictionaryViewModel : ViewModel() {
    private val repository by lazy { DatabaseModule.repository }

    private val _words = MutableStateFlow<List<DictionaryItem>>(emptyList())
    val words: StateFlow<List<DictionaryItem>> = _words

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private var currentPage = 0L
    private val pageSize = 20L
    private var isLastPage = false
    private var isLoading = false

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (isLoading || isLastPage) return

        isLoading = true
        viewModelScope.launch {
            try {
                val currentQuery = _query.value
                val offset = currentPage * pageSize
                
                val newWords = if (currentQuery.isBlank()) {
                    repository.getAllWordsPaginated(pageSize, offset)
                } else {
                    repository.searchWordsPaginated(currentQuery, pageSize, offset)
                }

                if (newWords.isEmpty()) {
                    isLastPage = true
                } else {
                    _words.value = _words.value + newWords
                    currentPage++
                }
                
                println("Cargada página $currentPage, total palabras: ${_words.value.size}")
            } catch (e: Exception) {
                println("Error cargando página: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun onSearch(newQuery: String) {
        _query.value = newQuery
        // Reset pagination when search changes
        currentPage = 0
        isLastPage = false
        _words.value = emptyList()
        loadNextPage()
    }
}
