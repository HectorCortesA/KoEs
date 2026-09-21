package com.hector.koes.model

data class DictionaryItem(
    val spanish: String,
    val korean: String,
    val romanization: String,
    val pronunciation: String,
    val ejemploKoreano: String,
    val ejemploSpanish: String
)

data class FullDictionaryItem(
    val id: Long,
    val wordSpanish: String,
    val wordCoreano: String,
    val romanization: String,
    val pronunciation: String,
    val tipo: String,
    val categoria: String,
    val definicion: String,
    val ejemploSpanish: String,
    val ejemploKoreano: String
)
