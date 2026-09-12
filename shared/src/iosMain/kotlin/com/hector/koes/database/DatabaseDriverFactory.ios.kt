package com.hector.koes.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(KoEsDatabase.Schema, "KoEsDatabase.db")
}
