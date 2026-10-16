package com.hector.koes.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUserDefaults

// CAMBIA este valor si tu App Group tiene otro identificador.
// Debe coincidir EXACTAMENTE con el App Group activado en los dos targets de Xcode.
private const val APP_GROUP = "group.com.hector.koes"
private const val COUNTS_KEY = "daily_word_counts"

@Composable
actual fun KoesWidgetSync(wordCounts: Map<Int, Int>) {
    val serialized = remember(wordCounts) {
        (0 until 60).joinToString(",") { (wordCounts[it] ?: 0).coerceAtLeast(0).toString() }
    }
    LaunchedEffect(serialized) {
        NSUserDefaults(suiteName = APP_GROUP)?.setObject(serialized, forKey = COUNTS_KEY)
        // La app Swift escucha este evento para solicitar a WidgetKit una actualización.
        NSNotificationCenter.defaultCenter.postNotificationName(
            aName = "KOES_WIDGET_UPDATED",
            `object` = null
        )
    }
}
