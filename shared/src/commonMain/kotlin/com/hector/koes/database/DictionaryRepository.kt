package com.hector.koes.database

import com.hector.koes.database.DictionaryItemEntity
import com.hector.koes.model.DictionaryItem
import com.hector.koes.model.FullDictionaryItem

class DictionaryRepository {
    private val database = KoEsDatabase(createDriver())
    private val queries = database.dictionaryQueries

    private fun DictionaryItemEntity.toDomain(): DictionaryItem {
        return DictionaryItem(
            spanish = wordSpanish,
            korean = wordCoreano,
            romanization = romanization,
            pronunciation = pronunciation,
            ejemploKoreano = ejemploKoreano,
            ejemploSpanish = ejemploSpanish
        )
    }

    private fun DictionaryItemEntity.toFullDomain(): FullDictionaryItem {
        return FullDictionaryItem(
            id = id,
            wordSpanish = wordSpanish,
            wordCoreano = wordCoreano,
            romanization = romanization,
            pronunciation = pronunciation,
            tipo = tipo,
            categoria = categoria,
            definicion = definicion,
            ejemploSpanish = ejemploSpanish,
            ejemploKoreano = ejemploKoreano
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

    fun getCategories(): List<String> {
        return queries.getCategories().executeAsList()
    }

    fun getRandomByCategory(categoria: String): FullDictionaryItem? {
        return queries.getRandomByCategory(categoria).executeAsOneOrNull()?.toFullDomain()
    }

    fun getRandomAll(): FullDictionaryItem? {
        return queries.getRandomAll().executeAsOneOrNull()?.toFullDomain()
    }
}
