package com.hector.koes.viewModel

import com.hector.koes.model.DictionaryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object FavoritesManager {
    private val _favorites = MutableStateFlow<List<DictionaryItem>>(emptyList())
    val favorites: StateFlow<List<DictionaryItem>> = _favorites

    fun isFavorite(item: DictionaryItem): Boolean {
        return _favorites.value.any { it.spanish == item.spanish && it.korean == item.korean }
    }

    fun toggleFavorite(item: DictionaryItem) {
        val current = _favorites.value.toMutableList()
        val index = current.indexOfFirst { it.spanish == item.spanish && it.korean == item.korean }
        if (index >= 0) {
            current.removeAt(index)
        } else {
            current.add(0, item)
        }
        _favorites.value = current
    }
}
