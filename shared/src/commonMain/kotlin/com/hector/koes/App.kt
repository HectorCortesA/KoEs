package com.hector.koes

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.hector.koes.View.CameraView
import com.hector.koes.View.DiccionaryView
import com.hector.koes.View.EditCameraView
import com.hector.koes.View.FavoriteView
import com.hector.koes.View.HangulView
import com.hector.koes.View.Home
import com.hector.koes.View.OnBoardingName
import com.hector.koes.View.OnboardingPreferences
import com.hector.koes.View.ScoreView
import com.hector.koes.View.SettingsView
import com.hector.koes.util.PreferencesStorage

@Composable
@Preview
fun App() {
    val isCompleted = remember { PreferencesStorage.isOnboardingCompleted() }
    var currentScreen by remember { mutableStateOf(if (isCompleted) "home" else "onboardingName") }
    var capturedPhotoBytes by remember { mutableStateOf<ByteArray?>(null) }

    MaterialTheme {
        when (currentScreen) {
            "onboardingName" -> OnBoardingName(
                onNext = {
                    currentScreen = "onboardingPreferences"
                }
            )
            "onboardingPreferences" -> OnboardingPreferences(
                onFinalize = {
                    currentScreen = "home"
                }
            )
            "home" -> Home(onNavigate = { currentScreen = it })
            "dictionary" -> DiccionaryView(onNavigate = { currentScreen = it })
            "hangul" -> HangulView(onNavigate = { currentScreen = it })
            "score" -> ScoreView(onNavigate = { currentScreen = it })
            "settings" -> SettingsView(onNavigate = { currentScreen = it })
            "favorite", "favorites", "favoriteView" -> FavoriteView(onNavigate = { currentScreen = it })
            "camera", "cameraView" -> CameraView(
                onNavigate = { currentScreen = it },
                onPhotoApproved = { photo ->
                    capturedPhotoBytes = photo
                    currentScreen = "editCamera"
                }
            )
            "editCamera", "editCameraView" -> EditCameraView(
                onNavigate = { currentScreen = it },
                photoBytes = capturedPhotoBytes
            )
            else -> Home(onNavigate = { currentScreen = it })
        }
    }
}
