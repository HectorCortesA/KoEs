package com.hector.koes.model

import kotlinx.serialization.Serializable

@Serializable
data class ProgressBackup(
    val completedWords: Set<String>,
    val activeDays: Set<Long>,
    val dailyCounts: Map<Long, Int>
)