@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.hector.koes.components.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIViewController

@Composable
actual fun rememberShareLauncher(): (text: String, imageBytes: ByteArray?) -> Unit {
    return remember {
        { text: String, imageBytes: ByteArray? ->
            val items = mutableListOf<Any>()
            if (text.isNotBlank()) items.add(text)

            // Compartimos una UIImage real, no únicamente el texto.
            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val data = imageBytes.usePinned { pinned ->
                    NSData.create(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
                }
                UIImage(data = data)?.let { items.add(it) }
            }

            if (items.isNotEmpty()) {
                val shareController = UIActivityViewController(
                    activityItems = items,
                    applicationActivities = null
                )

                // Presenter visible; funciona también cuando hay otra pantalla modal.
                var presenter: UIViewController? =
                    UIApplication.sharedApplication.keyWindow?.rootViewController
                while (presenter?.presentedViewController != null) {
                    presenter = presenter.presentedViewController
                }
                presenter?.presentViewController(
                    viewControllerToPresent = shareController,
                    animated = true,
                    completion = null
                )
            }
        }
    }
}
