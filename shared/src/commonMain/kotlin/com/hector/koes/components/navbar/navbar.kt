
package com.hector.koes.components.navbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hector.koes.resources.Res
import com.hector.koes.ui.theme.*
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.PI
import kotlin.math.hypot

private val GlassBorder = Brush.verticalGradient(
    colors = listOf(
        GlassBorderColorStart,
        GlassBorderColorEnd
    )
)

@Composable
fun Navbar(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit = {},
    onHoldChange: (Boolean) -> Unit = {},
    activeOption: String = "dictionary"
) {
    Row(
        modifier = modifier.safeContentPadding(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DictionaryWaterButton(
            activeOption = activeOption,
            onNavigate = onNavigate,
            onHoldChange = onHoldChange
        )

        NavButton(
            iconUri = "files/hg.svg",
            onClick = { onNavigate("hangul") }
        )

        NavButton(
            iconUri = "files/favorite.svg",
            onClick = { onNavigate("favorite") }
        )

        NavButton(
            iconUri = "files/home.svg",
            onClick = { onNavigate("home") }
        )

        NavButton(
            iconUri = "files/score.svg",
            onClick = { onNavigate("score") }
        )

        Spacer(modifier = Modifier.width(26.dp))

        NavButton(
            iconUri = "files/setting.svg",
            onClick = { onNavigate("settings") }
        )
    }
}

@Composable
fun DictionaryWaterButton(
    activeOption: String = "dictionary",
    onNavigate: (String) -> Unit,
    onHoldChange: (Boolean) -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var selected by remember {
        mutableStateOf(false)
    }

    val learnActive = activeOption == "oracion"

    // El icono principal depende de la selección global.
    val mainIcon = if (learnActive) {
        "files/learn.svg"
    } else {
        "files/dicionary.svg"
    }

    val secondaryIcon = if (learnActive) {
        "files/dicionary.svg"
    } else {
        "files/learn.svg"
    }

    val mainRoute = if (learnActive) {
        "oracion"
    } else {
        "dictionary"
    }

    val secondaryRoute = if (learnActive) {
        "dictionary"
    } else {
        "oracion"
    }

    val navigateState by rememberUpdatedState(onNavigate)
    val holdState by rememberUpdatedState(onHoldChange)
    val mainRouteState by rememberUpdatedState(mainRoute)
    val secondaryRouteState by rememberUpdatedState(secondaryRoute)

    val density = LocalDensity.current
    val configuration = LocalViewConfiguration.current

    val buttonPx = with(density) { 50.dp.toPx() }
    val optionCenterPx = with(density) { 89.dp.toPx() }
    val optionRadiusPx = with(density) { 25.dp.toPx() }

    val selectionProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(150),
        label = "WaterSelection"
    )

    val dropProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(320),
        label = "WaterDropPosition"
    )

    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        // Partículas que salen continuamente durante la pulsación.
        WaterParticles(
            active = expanded,
            modifier = Modifier.align(Alignment.Center)
        )

        // BOTÓN SECUNDARIO
        // AnimatedVisibility evita que quede visible en reposo.
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 64.dp * dropProgress),
            enter = fadeIn(
                animationSpec = tween(240)
            ) + scaleIn(
                initialScale = 0.25f,
                animationSpec = tween(320)
            ),
            exit = fadeOut(
                animationSpec = tween(150)
            ) + scaleOut(
                targetScale = 0.25f,
                animationSpec = tween(200)
            )
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = CardDictionaryBackgroundColor.copy(
                    alpha = 0.25f + 0.75f * selectionProgress
                ),
                border = BorderStroke(
                    width = 1.dp,
                    brush = GlassBorder
                ),
                shadowElevation = if (selected) 6.dp else 0.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    key(secondaryIcon) {
                        AsyncImage(
                            model = Res.getUri(secondaryIcon),
                            contentDescription = "Opción secundaria",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // BOTÓN PRINCIPAL
        Surface(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .pointerInput(
                    buttonPx,
                    optionCenterPx,
                    optionRadiusPx,
                    configuration.longPressTimeoutMillis
                ) {
                    awaitEachGesture {
                        val down = awaitFirstDown()

                        var quickTap = false
                        var cancelled = false

                        val completedBeforeHold = withTimeoutOrNull(
                            configuration.longPressTimeoutMillis
                        ) {
                            var finished = false

                            while (!finished) {
                                val event = awaitPointerEvent()

                                val change = event.changes
                                    .firstOrNull { it.id == down.id }

                                if (change == null) {
                                    cancelled = true
                                    finished = true
                                } else if (!change.pressed) {
                                    quickTap = !change.isConsumed
                                    finished = true
                                    change.consume()
                                } else {
                                    val dx =
                                        change.position.x - down.position.x
                                    val dy =
                                        change.position.y - down.position.y

                                    val distance = hypot(dx, dy)

                                    if (
                                        change.isConsumed ||
                                        distance > viewConfiguration.touchSlop
                                    ) {
                                        cancelled = true
                                        finished = true
                                    }
                                }
                            }

                            true
                        }

                        if (completedBeforeHold == true) {
                            if (quickTap && !cancelled) {
                                navigateState(mainRouteState)
                            }
                        } else {
                            // Pulsación prolongada.
                            expanded = true
                            selected = false
                            holdState(true)

                            var activateSecondary = false
                            var fingerDown = true

                            try {
                                while (fingerDown) {
                                    val event = awaitPointerEvent()

                                    val change = event.changes
                                        .firstOrNull { it.id == down.id }

                                    if (change == null) {
                                        fingerDown = false
                                    } else {
                                        val dx =
                                            change.position.x -
                                                    buttonPx / 2f

                                        val dy =
                                            change.position.y -
                                                    optionCenterPx

                                        // Detecta el círculo de la gota.
                                        selected =
                                            hypot(dx, dy) <= optionRadiusPx &&
                                                    dropProgress >= 0.8f

                                        if (!change.pressed) {
                                            activateSecondary =
                                                selected && !change.isConsumed

                                            fingerDown = false
                                        }

                                        change.consume()
                                    }
                                }
                            } finally {
                                expanded = false
                                selected = false
                                holdState(false)
                            }

                            if (activateSecondary) {
                                navigateState(secondaryRouteState)
                            }
                        }
                    }
                },
            shape = CircleShape,
            color = if (expanded) {
                NavbarWaterActiveGlowColor
            } else {
                Color.Transparent
            },
            border = BorderStroke(
                width = 1.dp,
                brush = GlassBorder
            )
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Fuerza a Compose a actualizar el SVG
                // cuando cambie la opción principal.
                key(mainIcon) {
                    AsyncImage(
                        model = Res.getUri(mainIcon),
                        contentDescription = if (learnActive) {
                            "Learn"
                        } else {
                            "Diccionario"
                        },
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NavButton(
    text: String? = null,
    iconUri: String? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            brush = GlassBorder
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            if (iconUri != null) {
                AsyncImage(
                    model = Res.getUri(iconUri),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            } else if (text != null) {
                Text(
                    text = text,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 180
)
@Composable
fun NavbarDictionaryPreview() {
    Navbar(activeOption = "dictionary")
}

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 180
)
@Composable
fun NavbarLearnPreview() {
    Navbar(activeOption = "oracion")
}
