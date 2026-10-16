package com.hector.koes.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.glance.appwidget.updateAll
import com.hector.koes.viewModel.UserProgressManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun KoesWidgetSync(wordCounts: Map<Int, Int>) {
    val context = LocalContext.current.applicationContext
    val isInitialized by UserProgressManager.isInitialized.collectAsState()

    val data = remember(wordCounts) {
        (0 until 60).joinToString(",") { (wordCounts[it] ?: 0).coerceAtLeast(0).toString() }
    }
    LaunchedEffect(data, isInitialized) {
        if (!isInitialized) return@LaunchedEffect
        println("KoesWidgetSync (Android): Syncing widget data, length=${data.length}")
        withContext(Dispatchers.IO) {
            context.getSharedPreferences("koes_calendar_widget", 0)
                .edit()
                .putString("daily_word_counts", data)
                .commit()
        }
        try {
            KoesCalendarWidget().updateAll(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
