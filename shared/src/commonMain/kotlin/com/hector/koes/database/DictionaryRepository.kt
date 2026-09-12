package com.hector.koes.database

import com.hector.koes.database.DictionaryItemEntity
import com.hector.koes.model.DictionaryItem

class DictionaryRepository {
    private val database = KoEsDatabase(createDriver())
    private val queries = database.dictionaryQueries

    private fun DictionaryItemEntity.toDomain(): DictionaryItem {
        return DictionaryItem(
            spanish = wordSpanish,
            korean = wordCoreano,
            romanization = romanization,
            pronunciation = pronunciation
        )
    }

    fun insertWord(item: DictionaryItem, tipo: String, categoria: String, definicion: String, ejSp: String, ejKr: String) {
        queries.insertWord(
            item.spanish,
            item.korean,
            item.romanization,
            item.pronunciation,
            tipo,
            categoria,
            definicion,
            ejSp,
            ejKr
        )
    }

    fun getAllWords(): List<DictionaryItem> {
        return queries.selectAll().executeAsList().map { it.toDomain() }
    }

    fun getAllWordsPaginated(limit: Long, offset: Long): List<DictionaryItem> {
        return queries.selectAllPaginated(limit, offset).executeAsList().map { it.toDomain() }
    }

    fun searchWords(query: String): List<DictionaryItem> {
        return queries.searchWords(query = query).executeAsList().map { it.toDomain() }
    }

    fun searchWordsPaginated(query: String, limit: Long, offset: Long): List<DictionaryItem> {
        return queries.searchWordsPaginated(query = query, limit = limit, offset = offset).executeAsList().map { it.toDomain() }
    }

    fun countWords(): Long {
        return queries.countWords().executeAsOne()
    }

    fun deleteAll() {
        queries.deleteAll()
    }
}
