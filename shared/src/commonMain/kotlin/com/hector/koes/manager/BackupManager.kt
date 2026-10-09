package com.hector.koes.manager

import com.hector.koes.model.Backup
import com.hector.koes.model.Profile
import com.hector.koes.model.ProgressBackup
import com.hector.koes.viewModel.UserProgressManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object BackupManager {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    // Convierte los datos del usuario a JSON
    fun exportToJson(profile: Profile): String {

        val progress = ProgressBackup(
            completedWords = UserProgressManager.getCompletedWords(),
            activeDays = UserProgressManager.getActiveDays(),
            dailyCounts = UserProgressManager.getWordCountsByDay()
        )

        val backup = Backup(
            version = 1,
            profile = profile,
            progress = progress
        )

        return json.encodeToString(backup)
    }

    // Lee un JSON y restaura el progreso
    fun importFromJson(jsonString: String): Profile {

        val backup = json.decodeFromString<Backup>(jsonString)

        require(backup.version == 1) {
            "Versión de respaldo no compatible"
        }

        UserProgressManager.restoreProgress(
            completedWords = backup.progress.completedWords,
            activeDays = backup.progress.activeDays,
            dailyCounts = backup.progress.dailyCounts
        )

        // Devuelve el perfil para guardarlo en tu almacenamiento local
        return backup.profile
    }
}