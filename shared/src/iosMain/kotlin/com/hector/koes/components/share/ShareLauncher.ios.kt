package com.hector.koes.components.share

import androidx.compose.runtime.Composable
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberShareLauncher(): (text: String, imageBytes: ByteArray?) -> Unit {
    return { text, imageBytes ->
        val items = mutableListOf<Any>()
        if (text.isNotEmpty()) {
            items.add(text)
        }

        val activityViewController = UIActivityViewController(
            activityItems = items,
            applicationActivities = null
        )

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }
}
