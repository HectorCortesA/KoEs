package com.hector.koes.backup

interface BackupFilePicker {

    suspend fun exportBackup(json: String): Boolean

    suspend fun importBackup(): String?
}