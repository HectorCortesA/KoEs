package com.hector.koes.model

import kotlinx.serialization.Serializable

@Serializable
data class Progreso(
    val completedWords: List<String> = emptyList(),
    val activeDays: List<String> = emptyList(),
    val dailyWordCounts: Map<String, Int> = emptyMap()
)