package com.hector.koes.viewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UserProgressManager {

    private val _completedWords = MutableStateFlow<Set<String>>(emptySet())
    val completedWords: StateFlow<Set<String>> = _completedWords

    private val _wordsLearnedCount = MutableStateFlow(0)
    val wordsLearnedCount: StateFlow<Int> = _wordsLearnedCount

    private val _activeDays = MutableStateFlow<Set<Long>>(emptySet())
    val activeDays: StateFlow<Set<Long>> = _activeDays

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak

    // Mapea la celda del calendario (0..59) a la cantidad de palabras resueltas ese día
    private val _dailyWordCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val dailyWordCounts: StateFlow<Map<Int, Int>> = _dailyWordCounts

    private var startEpochDay: Long? = null

    private fun getTodayEpochDay(): Long {
        return kotlin.time.TimeSource.Monotonic.markNow().elapsedNow().inWholeDays
    }

    fun recordWordCompleted(word: String) {
        val trimmed = word.trim()
        if (trimmed.isEmpty()) return

        // 1. Palabras aprendidas (únicas)
        val newWords = _completedWords.value + trimmed
        _completedWords.value = newWords
        _wordsLearnedCount.value = newWords.size

        // 2. Día de actividad actual
        val todayEpoch = getTodayEpochDay()
        if (startEpochDay == null) {
            startEpochDay = todayEpoch
        }

        val newActiveDays = _activeDays.value + todayEpoch
        _activeDays.value = newActiveDays

        // 3. Cálculo de Racha (días consecutivos)
        calculateStreak(newActiveDays, todayEpoch)

        // 4. Mapeo comenzando desde la primera celda del mapa (celda 0)
        val dayOffset = (todayEpoch - startEpochDay!!).toInt()
        val cellIndex = (dayOffset % 60)
        val currentCounts = _dailyWordCounts.value.toMutableMap()
        val previousCount = currentCounts[cellIndex] ?: 0
        currentCounts[cellIndex] = previousCount + 1
        _dailyWordCounts.value = currentCounts
    }

    private fun calculateStreak(activeDaysSet: Set<Long>, todayEpoch: Long) {
        var streak = 0
        var checkDay = todayEpoch

        if (!activeDaysSet.contains(todayEpoch)) {
            checkDay = todayEpoch - 1
        }

        while (activeDaysSet.contains(checkDay)) {
            streak++
            checkDay--
        }

        _currentStreak.value = if (streak == 0 && activeDaysSet.isNotEmpty()) 1 else streak
    }
}
