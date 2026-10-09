package com.hector.koes.model

import kotlinx.serialization.Serializable

@Serializable
data class Backup(
    val version: Int = 1,
    val profile: Profile,
    val progress: ProgressBackup
)