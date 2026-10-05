package com.hector.koes.util

import platform.Foundation.NSUserDefaults

actual object PreferencesStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun isOnboardingCompleted(): Boolean {
        return defaults.boolForKey("onboarding_completed")
    }

    actual fun setOnboardingCompleted(completed: Boolean) {
        defaults.setBool(completed, forKey = "onboarding_completed")
        defaults.synchronize()
    }

    actual fun getUserName(): String {
        return defaults.stringForKey("user_name") ?: ""
    }

    actual fun setUserName(name: String) {
        defaults.setObject(name, forKey = "user_name")
        defaults.synchronize()
    }

    actual fun getUserPhotoUrl(): String {
        return defaults.stringForKey("user_photo_url") ?: ""
    }

    actual fun setUserPhotoUrl(url: String) {
        defaults.setObject(url, forKey = "user_photo_url")
        defaults.synchronize()
    }
}
