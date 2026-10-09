package com.hector.koes.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberBackupImporter(onResult: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)
                    ?.bufferedReader()
                    ?.use { it.readText() }
                onResult(content)
            } catch (_: Exception) {
                onResult(null)
            }
        } else {
            onResult(null)
        }
    }

    return {
        try {
            launcher.launch(arrayOf("*/*"))
        } catch (_: Exception) {
            onResult(null)
        }
    }
}
