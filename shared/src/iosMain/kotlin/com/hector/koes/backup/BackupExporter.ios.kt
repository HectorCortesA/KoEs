package com.hector.koes.backup

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberBackupExporter(onResult: (Boolean) -> Unit): (String, String) -> Unit {
    var documentDelegate: UIDocumentPickerDelegateProtocol? = null

    return { json, fileName ->
        try {
            val fileUrl = NSURL.fileURLWithPath(NSTemporaryDirectory() + fileName)
            val data = (json as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            if (data != null && data.writeToURL(fileUrl, true)) {
                val picker = UIDocumentPickerViewController(forExportingURLs = listOf(fileUrl), asCopy = true)

                val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                        onResult(true)
                        documentDelegate = null
                    }
                    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                        onResult(false)
                        documentDelegate = null
                    }
                }
                documentDelegate = delegate
                picker.delegate = delegate

                val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
                rootVC?.presentViewController(picker, animated = true, completion = null)
            } else {
                onResult(false)
            }
        } catch (_: Exception) {
            onResult(false)
        }
    }
}
