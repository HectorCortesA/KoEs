package com.hector.koes.widgets

import androidx.compose.runtime.Composable

/**
 * Incluye KoesWidgetSync(dailyWordCounts) en la pantalla principal de KOES.
 * No modifica HeatCalendar.kt ni tu UserProgressManager.
 */
@Composable
expect fun KoesWidgetSync(wordCounts: Map<Int, Int>)
