package com.hector.koes.database

import kotlin.native.concurrent.ThreadLocal

object DatabaseModule {
    // Lazy initialization para no crear la DB hasta que se necesite
    val repository: DictionaryRepository by lazy {
        DictionaryRepository()
    }
}
