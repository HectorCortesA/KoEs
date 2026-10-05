package com.hector.koes.util

expect object PreferencesStorage {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)

    fun getUserName(): String
    fun setUserName(name: String)

    fun getUserPhotoUrl(): String
    fun setUserPhotoUrl(url: String)
}
