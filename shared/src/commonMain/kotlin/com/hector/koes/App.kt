
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

    // ----------------------------------------
    // 1. ONBOARDING
    // ----------------------------------------

    val isCompleted = remember {
        PreferencesStorage.isOnboardingCompleted()
    }

    var currentScreen by remember {
        mutableStateOf(
            if (isCompleted) "home"
            else "onboardingName"
        )
    }

    // ----------------------------------------
    // 2. CÁMARA
    // ----------------------------------------

    var capturedPhotoBytes by remember {
        mutableStateOf<ByteArray?>(null)
    }

    // ----------------------------------------
    // 3. BOTÓN PRINCIPAL DEL NAVBAR
    // ----------------------------------------

    // Diccionario es la opción inicial.
    // Cuando se selecciona Learn, cambia a "oracion".
    var activeNavbarOption by remember {
        mutableStateOf("dictionary")
    }

    // ----------------------------------------
    // 4. EFECTO BLUR GLOBAL
    // ----------------------------------------

    var isNavbarHolding by remember {
        mutableStateOf(false)
    }

    val blurRadius by animateDpAsState(
        targetValue = if (isNavbarHolding) {
            15.dp
        } else {
            0.dp
        },
        animationSpec = tween(
            durationMillis = 250
        ),
        label = "GlobalNavbarBlur"
    )

    // ----------------------------------------
    // 5. PANTALLAS CON NAVBAR
    // ----------------------------------------

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

    // ----------------------------------------
    // 6. NAVEGACIÓN
    // ----------------------------------------

    val navigate: (String) -> Unit = { route ->

        // Desactivar blur al navegar.
        isNavbarHolding = false

        // Intercambiar los botones únicamente cuando
        // el usuario selecciona Diccionario o Learn.
        if (
            route == "dictionary" ||
            route == "oracion"
        ) {
            activeNavbarOption = route
        }

        // Cambiar pantalla.
        currentScreen = route
    }

    // ----------------------------------------
    // 7. INTERFAZ PRINCIPAL
    // ----------------------------------------

    MaterialTheme {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // CONTENIDO DE LA PANTALLA
            // El blur se aplica a la vista actual,
            // pero NO al Navbar global.

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        radius = if (showGlobalNavbar) {
                            blurRadius
                        } else {
                            0.dp
                        }
                    )
            ) {

                when (currentScreen) {

                    // --------------------------------
                    // ONBOARDING
                    // --------------------------------

                    "onboardingName" -> {
                        OnBoardingName(
                            onNext = {
                                navigate("onboardingPreferences")
                            }
                        )
                    }

                    "onboardingPreferences" -> {
                        OnboardingPreferences(
                            onFinalize = {
                                navigate("home")
                            }
                        )
                    }

                    // --------------------------------
                    // HOME
                    // --------------------------------

                    "home" -> {
                        Home(
                            onNavigate = navigate,
                            showNavbar = false
                        )
                    }

                    // --------------------------------
                    // DICCIONARIO
                    // --------------------------------

                    "dictionary" -> {
                        DiccionaryView(
                            onNavigate = navigate,
                            showNavbar = false
                        )
                    }

                    // --------------------------------
                    // LEARN / ORACIONES
                    // --------------------------------

                    "oracion" -> {
                        LearnView(
                            onNavigate = navigate,
                            showNavbar = false,


                        )
                    }

                    // --------------------------------
                    // HANGUL
                    // --------------------------------

                    "hangul" -> {
                        HangulView(
                            onNavigate = navigate,
                            showNavbar = false
                        )
                    }

                    // --------------------------------
                    // SCORE
                    // --------------------------------

                    "score" -> {
                        ScoreView(
                            onNavigate = navigate,
                            showNavbar = false
                        )
                    }

                    // --------------------------------
                    // SETTINGS
                    // --------------------------------

                    "settings" -> {
                        SettingsView(
                            onNavigate = navigate,
                            showNavbar = false
                        )
                    }

                    // --------------------------------
                    // FAVORITOS
                    // --------------------------------

                    "favorite",
                    "favorites",
                    "favoriteView" -> {
                        FavoriteView(
                            onNavigate = navigate,
                            showNavbar = false
                        )
                    }

                    // --------------------------------
                    // CÁMARA
                    // --------------------------------

                    "camera",
                    "cameraView" -> {
                        CameraView(
                            onNavigate = navigate,
                            onPhotoApproved = { photo ->
                                capturedPhotoBytes = photo
                                navigate("editCamera")
                            }
                        )
                    }

                    // --------------------------------
                    // EDITAR FOTOGRAFÍA
                    // --------------------------------

                    "editCamera",
                    "editCameraView" -> {
                        EditCameraView(
                            onNavigate = navigate,
                            photoBytes = capturedPhotoBytes
                        )
                    }

                    // --------------------------------
                    // PANTALLA PREDETERMINADA
                    // --------------------------------

                    else -> {
                        Home(
                            onNavigate = navigate
                        )
                    }
                }
            }

            // ----------------------------------------
            // 8. NAVBAR GLOBAL
            // ----------------------------------------

            // Se dibuja encima del contenido.
            // No recibe el efecto blur.
            // Conserva el botón intercambiable.

            if (showGlobalNavbar) {

                Navbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 10.dp),

                    // Botón actualmente seleccionado.
                    activeOption = activeNavbarOption,

                    // Navegación.
                    onNavigate = navigate,

                    // Control del blur global.
                    onHoldChange = { holding ->
                        isNavbarHolding = holding
                    }
                )
            }
        }
    }
}
