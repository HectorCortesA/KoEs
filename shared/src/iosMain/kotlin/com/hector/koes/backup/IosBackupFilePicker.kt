
package com.hector.koes.backup

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CompletableDeferred
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.writeToURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class IosBackupFilePicker(
    private val viewController: UIViewController
) : BackupFilePicker {

    private var pendingImport: CompletableDeferred<String?>? = null
    private var pendingExport: CompletableDeferred<Boolean>? = null

    // Mantiene vivo el delegado mientras el selector está abierto
    private var documentDelegate: UIDocumentPickerDelegateProtocol? = null

    override suspend fun exportBackup(json: String): Boolean {
        val result = CompletableDeferred<Boolean>()
        pendingExport = result

        val fileUrl = NSURL.fileURLWithPath(
            NSTemporaryDirectory() + "KoEs.data"
        )

        val data = (json as NSString)
            .dataUsingEncoding(NSUTF8StringEncoding)

        if (data == null || !data.writeToURL(fileUrl, true)) {
            pendingExport = null
            return false
        }

        val picker = UIDocumentPickerViewController(
            forExportingURLs = listOf(fileUrl),
            asCopy = true
        )

        val delegate = object :
            NSObject(),
            UIDocumentPickerDelegateProtocol {

            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {
                pendingExport?.complete(true)
                pendingExport = null
                documentDelegate = null
            }

            override fun documentPickerWasCancelled(
                controller: UIDocumentPickerViewController
            ) {
                pendingExport?.complete(false)
                pendingExport = null
                documentDelegate = null
            }
        }

        documentDelegate = delegate
        picker.delegate = delegate

        viewController.presentViewController(
            picker,
            animated = true,
            completion = null
        )

        return result.await()
    }

    override suspend fun importBackup(): String? {
        val result = CompletableDeferred<String?>()
        pendingImport = result

        val picker = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(
                platform.UniformTypeIdentifiers.UTTypeData
            ),
            asCopy = true
        )

        val delegate = object :
            NSObject(),
            UIDocumentPickerDelegateProtocol {

            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {
                val url = didPickDocumentsAtURLs
                    .firstOrNull() as? NSURL

                val content: String? = url?.let { fileUrl ->

                    val hasAccess =
                        fileUrl.startAccessingSecurityScopedResource()

                    try {
                        val data = NSData.create(
                            contentsOfURL = fileUrl
                        )

                        data?.let { bytes ->
                            NSString.create(
                                data = bytes,
                                encoding = NSUTF8StringEncoding
                            )?.toString()
                        }
                    } finally {
                        if (hasAccess) {
                            fileUrl.stopAccessingSecurityScopedResource()
                        }
                    }
                }

                pendingImport?.complete(content)
                pendingImport = null
                documentDelegate = null
            }

            override fun documentPickerWasCancelled(
                controller: UIDocumentPickerViewController
            ) {
                pendingImport?.complete(null)
                pendingImport = null
                documentDelegate = null
            }
        }

        documentDelegate = delegate
        picker.delegate = delegate

        viewController.presentViewController(
            picker,
            animated = true,
            completion = null
        )

        return result.await()
    }
}
