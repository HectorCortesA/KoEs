package com.hector.koes.backup

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.UniformTypeIdentifiers.UTTypeData
import platform.UIKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberBackupImporter(onResult: (String?) -> Unit): () -> Unit {
    var documentDelegate: UIDocumentPickerDelegateProtocol? = null

    return {
        try {
            val picker = UIDocumentPickerViewController(
                forOpeningContentTypes = listOf(UTTypeData),
                asCopy = true
            )

            val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                    val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                    val content = url?.let { fileUrl ->
                        val hasAccess = fileUrl.startAccessingSecurityScopedResource()
                        try {
                            val data = NSData.create(contentsOfURL = fileUrl)
                            data?.let { bytes ->
                                NSString.create(data = bytes, encoding = NSUTF8StringEncoding)?.toString()
                            }
                        } finally {
                            if (hasAccess) {
                                fileUrl.stopAccessingSecurityScopedResource()
                            }
                        }
                    }
                    onResult(content)
                    documentDelegate = null
                }

                override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                    onResult(null)
                    documentDelegate = null
                }
            }

            documentDelegate = delegate
            picker.delegate = delegate

            val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootVC?.presentViewController(picker, animated = true, completion = null)
        } catch (_: Exception) {
            onResult(null)
        }
    }
}
