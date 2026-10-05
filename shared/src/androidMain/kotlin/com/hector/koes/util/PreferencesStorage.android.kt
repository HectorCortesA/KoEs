package com.hector.koes.util

import android.content.Context
import com.hector.koes.database.AndroidContext

actual object PreferencesStorage {
    private val prefs by lazy {
        AndroidContext.context.getSharedPreferences("koes_prefs", Context.MODE_PRIVATE)
    }

    actual fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean("onboarding_completed", false)
    }

    actual fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
    }

    actual fun getUserName(): String {
        return prefs.getString("user_name", "") ?: ""
    }

    actual fun setUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }

    actual fun getUserPhotoUrl(): String {
        return prefs.getString("user_photo_url", "") ?: ""
    }

    actual fun setUserPhotoUrl(url: String) {
        prefs.edit().putString("user_photo_url", url).apply()
    }
}
