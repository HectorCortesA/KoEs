package com.hector.koes.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val nameProfile: String,
    val photoUrl: String
)