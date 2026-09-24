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
import com.hector.koes.View.HangulView
import com.hector.koes.View.Home
import com.hector.koes.View.ScoreView
import com.hector.koes.View.SettingsView

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf("home") }
    var capturedPhotoBytes by remember { mutableStateOf<ByteArray?>(null) }

    MaterialTheme {
        when (currentScreen) {
            "home" -> Home(onNavigate = { currentScreen = it })
            "dictionary" -> DiccionaryView(onNavigate = { currentScreen = it })
            "hangul" -> HangulView(onNavigate = { currentScreen = it })
            "score" -> ScoreView(onNavigate = { currentScreen = it })
            "settings" -> SettingsView(onNavigate = { currentScreen = it })
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
