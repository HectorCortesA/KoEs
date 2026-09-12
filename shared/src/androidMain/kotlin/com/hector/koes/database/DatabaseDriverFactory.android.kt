package com.hector.koes.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

// Usaremos un objeto para guardar el contexto en Android
object AndroidContext {
    lateinit var context: Context
}

actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(KoEsDatabase.Schema, AndroidContext.context, "KoEsDatabase.db")
}
