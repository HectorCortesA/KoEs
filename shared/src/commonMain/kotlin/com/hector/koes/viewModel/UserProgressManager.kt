package com.hector.koes.viewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object UserProgressManager {

    // Palabras aprendidas (únicas)
    private val _completedWords =
        MutableStateFlow<Set<String>>(emptySet())

    val completedWords: StateFlow<Set<String>> = _completedWords

    // Cantidad total de palabras aprendidas
    private val _wordsLearnedCount = MutableStateFlow(0)

    val wordsLearnedCount: StateFlow<Int> = _wordsLearnedCount

    // Días reales en los que hubo actividad
    private val _activeDays =
        MutableStateFlow<Set<Long>>(emptySet())

    val activeDays: StateFlow<Set<Long>> = _activeDays

    // Racha actual
    private val _currentStreak = MutableStateFlow(0)

    val currentStreak: StateFlow<Int> = _currentStreak

    // Conteo por celda del calendario (0..59)
    private val _dailyWordCounts =
        MutableStateFlow<Map<Int, Int>>(emptyMap())

    val dailyWordCounts: StateFlow<Map<Int, Int>> =
        _dailyWordCounts

    // Fecha de inicio de actividad
    private var startEpochDay: Long? = null

    // Conteos asociados a fechas reales
    private val wordCountsByDay = mutableMapOf<Long, Int>()

    // Número de celdas del calendario
    private const val CALENDAR_DAYS = 60

    /**
     * Obtiene el día actual usando la fecha local del dispositivo.
     * Funciona en Android e iOS.
     */
    @OptIn(ExperimentalTime::class)
    private fun getTodayEpochDay(): Long {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        return today.toEpochDays().toLong()
    }

    /**
     * Registra una palabra completada.
     */
    fun recordWordCompleted(word: String) {
        val trimmed = word.trim()

        if (trimmed.isEmpty()) return

        // 1. Guardar palabra aprendida
        val newWords = _completedWords.value + trimmed

        _completedWords.value = newWords
        _wordsLearnedCount.value = newWords.size

        // 2. Obtener fecha real
        val todayEpoch = getTodayEpochDay()

        if (startEpochDay == null) {
            startEpochDay = todayEpoch
        }

        // 3. Registrar día activo
        val newActiveDays = _activeDays.value + todayEpoch

        _activeDays.value = newActiveDays

        // 4. Actualizar conteo de palabras del día
        val previousCount = wordCountsByDay[todayEpoch] ?: 0

        wordCountsByDay[todayEpoch] = previousCount + 1

        // 5. Calcular racha
        calculateStreak(newActiveDays, todayEpoch)

        // 6. Actualizar calendario
        updateCalendar()
    }

    /**
     * Calcula la racha de días consecutivos.
     */
    private fun calculateStreak(
        activeDaysSet: Set<Long>,
        todayEpoch: Long
    ) {
        var checkDay = todayEpoch

        // Si hoy no hubo actividad, comprobar ayer
        if (!activeDaysSet.contains(todayEpoch)) {
            checkDay = todayEpoch - 1
        }

        var streak = 0

        while (activeDaysSet.contains(checkDay)) {
            streak++
            checkDay--
        }

        _currentStreak.value = streak
    }

    /**
     * Actualiza las 60 celdas del calendario.
     *
     * Mantiene el comportamiento original:
     * primera actividad en celda 0 y avance
     * de una celda por día.
     */
    private fun updateCalendar() {
        val firstDay = startEpochDay ?: return

        val counts = mutableMapOf<Int, Int>()

        wordCountsByDay.forEach { (day, count) ->

            val offset = day - firstDay

            if (offset >= 0) {
                val cellIndex =
                    (offset % CALENDAR_DAYS).toInt()

                counts[cellIndex] = count
            }
        }

        _dailyWordCounts.value = counts
    }

    /**
     * Devuelve las palabras aprendidas.
     * Útil para generar el respaldo JSON.
     */
    fun getCompletedWords(): Set<String> {
        return _completedWords.value
    }

    /**
     * Devuelve los días de actividad.
     */
    fun getActiveDays(): Set<Long> {
        return _activeDays.value
    }

    /**
     * Devuelve los conteos por fecha real.
     */
    fun getWordCountsByDay(): Map<Long, Int> {
        return wordCountsByDay.toMap()
    }

    /**
     * Restaura el progreso desde un respaldo.
     */
    fun restoreProgress(
        completedWords: Set<String>,
        activeDays: Set<Long>,
        dailyCounts: Map<Long, Int>
    ) {
        _completedWords.value = completedWords
        _wordsLearnedCount.value = completedWords.size

        _activeDays.value = activeDays

        wordCountsByDay.clear()
        wordCountsByDay.putAll(dailyCounts)

        startEpochDay = (
                activeDays.minOrNull()
                    ?: dailyCounts.keys.minOrNull()
                )

        calculateStreak(
            activeDaysSet = activeDays,
            todayEpoch = getTodayEpochDay()
        )

        updateCalendar()
    }

    /**
     * Reinicia el progreso del usuario.
     */
    fun resetProgress() {
        _completedWords.value = emptySet()
        _wordsLearnedCount.value = 0
        _activeDays.value = emptySet()
        _currentStreak.value = 0
        _dailyWordCounts.value = emptyMap()

        wordCountsByDay.clear()
        startEpochDay = null
    }
}