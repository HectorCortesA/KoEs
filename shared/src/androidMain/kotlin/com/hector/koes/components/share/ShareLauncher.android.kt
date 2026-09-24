package com.hector.koes.components.share

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun rememberShareLauncher(): (text: String, imageBytes: ByteArray?) -> Unit {
    val context = LocalContext.current

    return { text, imageBytes ->
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (imageBytes != null) "image/png" else "text/plain"
            if (text.isNotEmpty()) {
                putExtra(Intent.EXTRA_TEXT, text)
            }
            if (imageBytes != null) {
                try {
                    val cachePath = File(context.cacheDir, "images")
                    cachePath.mkdirs()
                    val file = File(cachePath, "shared_image.png")
                    val stream = FileOutputStream(file)
                    stream.write(imageBytes)
                    stream.close()

                    val contentUri: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        val chooserIntent = Intent.createChooser(intent, "Compartir con:")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}
