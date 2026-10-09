
package com.hector.koes.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberBackupExporter(
    onResult: (Boolean) -> Unit
): (String, String) -> Unit {

    val context = LocalContext.current
    val currentOnResult by rememberUpdatedState(onResult)

    var pendingJson by remember {
        mutableStateOf<String?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/octet-stream"
        )
    ) { uri ->

        val json = pendingJson
        pendingJson = null

        if (uri == null || json == null) {
            currentOnResult(false)
        } else {
            try {
                val saved = context.contentResolver
                    .openOutputStream(uri)
                    ?.use { outputStream ->

                        outputStream.write(
                            json.encodeToByteArray()
                        )

                        outputStream.flush()
                        true
                    } ?: false

                currentOnResult(saved)

            } catch (e: Exception) {
                currentOnResult(false)
            }
        }
    }

    return remember(launcher) {
        { json: String, fileName: String ->

            if (pendingJson == null) {
                pendingJson = json

                try {
                    launcher.launch(fileName)
                } catch (e: Exception) {
                    pendingJson = null
                    currentOnResult(false)
                }
            }
        }
    }
}
