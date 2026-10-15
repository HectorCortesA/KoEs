
package com.hector.koes

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.hector.koes.View.CameraView
import com.hector.koes.View.DiccionaryView
import com.hector.koes.View.EditCameraView
import com.hector.koes.View.FavoriteView
import com.hector.koes.View.HangulView
import com.hector.koes.View.Home
import com.hector.koes.View.LearnView
import com.hector.koes.View.OnBoardingName
import com.hector.koes.View.OnboardingPreferences
import com.hector.koes.View.ScoreView
import com.hector.koes.View.SettingsView
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.util.PreferencesStorage

@Composable
@Preview
fun App() {
    val isCompleted = remember {
        PreferencesStorage.isOnboardingCompleted()
    }

    var currentScreen by remember {
        mutableStateOf(
            if (isCompleted) "home" else "onboardingName"
        )
    }

    var capturedPhotoBytes by remember {
        mutableStateOf<ByteArray?>(null)
    }

    // Última opción seleccionada del botón intercambiable.
    var activeNavbarOption by remember {
        mutableStateOf("dictionary")
    }

    // Control global del desenfoque.
    var isNavbarHolding by remember {
        mutableStateOf(false)
    }

    val blurRadius by animateDpAsState(
        targetValue = if (isNavbarHolding) 15.dp else 0.dp,
        animationSpec = tween(durationMillis = 250),
        label = "NavbarBlur"
    )

    val showGlobalNavbar = currentScreen in setOf(
        "home",
        "dictionary",
        "oracion",
        "hangul",
        "score",
        "settings",
        "favorite",
        "favorites",
        "favoriteView"
    )

    val navigate: (String) -> Unit = { route ->
        isNavbarHolding = false

        // Sólo estas dos rutas intercambian los iconos.
        if (route == "dictionary" || route == "oracion") {
            activeNavbarOption = route
        }

        currentScreen = route
    }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // La pantalla actual se desenfoca durante
            // la pulsación prolongada.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        if (showGlobalNavbar) blurRadius
                        else 0.dp
                    )
            ) {
                when (currentScreen) {

                    "onboardingName" -> OnBoardingName(
                        onNext = {
                            navigate("onboardingPreferences")
                        }
                    )

                    "onboardingPreferences" -> OnboardingPreferences(
                        onFinalize = {
                            navigate("home")
                        }
                    )

                    "home" -> Home(
                        onNavigate = navigate
                    )

                    "dictionary" -> DiccionaryView(
                        onNavigate = navigate
                    )

                    "oracion" -> LearnView(
                        onNavigate = navigate,
                        showNavbar = false
                    )

                    "hangul" -> HangulView(
                        onNavigate = navigate
                    )

                    "score" -> ScoreView(
                        onNavigate = navigate
                    )

                    "settings" -> SettingsView(
                        onNavigate = navigate
                    )

                    "favorite",
                    "favorites",
                    "favoriteView" -> FavoriteView(
                        onNavigate = navigate
                    )

                    "camera", "cameraView" -> CameraView(
                        onNavigate = navigate,
                        onPhotoApproved = { photo ->
                            capturedPhotoBytes = photo
                            navigate("editCamera")
                        }
                    )

                    "editCamera",
                    "editCameraView" -> EditCameraView(
                        onNavigate = navigate,
                        photoBytes = capturedPhotoBytes
                    )

                    else -> Home(
                        onNavigate = navigate
                    )
                }
            }

            // Único Navbar por encima del contenido.
            if (showGlobalNavbar) {
                Navbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 10.dp),
                    activeOption = activeNavbarOption,
                    onNavigate = navigate,
                    onHoldChange = { holding ->
                        isNavbarHolding = holding
                    }
                )
            }
        }
    }
}
