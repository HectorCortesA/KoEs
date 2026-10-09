package com.hector.koes.backup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.CompletableDeferred

class AndroidBackupFilePicker(
    private val activity: Activity
) : BackupFilePicker {

    private var pendingResult: CompletableDeferred<Uri?>? = null

    override suspend fun exportBackup(json: String): Boolean {
        val result = CompletableDeferred<Uri?>()
        pendingResult = result

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "KoEs.data")
        }

        activity.startActivityForResult(intent, EXPORT_REQUEST)

        val uri = result.await() ?: return false

        return try {
            activity.contentResolver.openOutputStream(uri)?.use {
                it.write(json.toByteArray(Charsets.UTF_8))
            } != null
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun importBackup(): String? {
        val result = CompletableDeferred<Uri?>()
        pendingResult = result

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        activity.startActivityForResult(intent, IMPORT_REQUEST)

        val uri = result.await() ?: return null

        return try {
            activity.contentResolver.openInputStream(uri)
                ?.bufferedReader()
                ?.use { it.readText() }
        } catch (e: Exception) {
            null
        }
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == EXPORT_REQUEST ||
            requestCode == IMPORT_REQUEST
        ) {
            pendingResult?.complete(
                if (resultCode == Activity.RESULT_OK) data?.data else null
            )
            pendingResult = null
        }
    }

    companion object {
        const val EXPORT_REQUEST = 1001
        const val IMPORT_REQUEST = 1002
    }
}