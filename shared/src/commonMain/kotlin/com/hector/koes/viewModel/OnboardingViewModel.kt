package com.hector.koes.viewModel

import androidx.lifecycle.ViewModel
import com.hector.koes.util.PreferencesStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OnboardingViewModel : ViewModel() {
    private val _userName = MutableStateFlow(PreferencesStorage.getUserName())
    val userName: StateFlow<String> = _userName

    private val _userPhotoUrl = MutableStateFlow(PreferencesStorage.getUserPhotoUrl())
    val userPhotoUrl: StateFlow<String> = _userPhotoUrl

    private val _isOnboardingCompleted = MutableStateFlow(PreferencesStorage.isOnboardingCompleted())
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted

    fun setUserName(name: String) {
        _userName.value = name
        PreferencesStorage.setUserName(name)
    }

    fun setUserPhotoUrl(url: String) {
        _userPhotoUrl.value = url
        PreferencesStorage.setUserPhotoUrl(url)
    }

    fun completeOnboarding() {
        PreferencesStorage.setOnboardingCompleted(true)
        _isOnboardingCompleted.value = true
    }
}
