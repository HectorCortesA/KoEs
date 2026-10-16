package com.hector.koes.util

import platform.Foundation.NSUserDefaults

actual object PreferencesStorage {
    private val defaults = NSUserDefaults(suiteName = "group.com.hector.koes")

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

    actual fun getProgressJson(): String {
        val jsonStr = defaults.stringForKey("progress_json") ?: ""
        println("PreferencesStorage (iOS): getProgressJson() -> length = ${jsonStr.length}, content = $jsonStr")
        return jsonStr
    }

    actual fun setProgressJson(json: String) {
        defaults.setObject(json, forKey = "progress_json")
        val success = defaults.synchronize()
        println("PreferencesStorage (iOS): setProgressJson() -> length = ${json.length}, success = $success")
    }
}
