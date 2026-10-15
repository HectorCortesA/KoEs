
package com.hector.koes.components.navbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hector.koes.resources.Res
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull

private val GlassBorder = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.1f)
    )
)

@Composable
fun Navbar(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit = {},
    onHoldChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = modifier.safeContentPadding(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        DictionaryWaterButton(
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

        Spacer(
            modifier = Modifier.width(26.dp)
        )

        NavButton(
            iconUri = "files/setting.svg",
            onClick = { onNavigate("settings") }
        )
    }
}

@Composable
fun DictionaryWaterButton(
    onNavigate: (String) -> Unit,
    onHoldChange: (Boolean) -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var selected by remember {
        mutableStateOf(false)
    }

    val density = LocalDensity.current
    val configuration = LocalViewConfiguration.current

    val buttonPx = with(density) {
        50.dp.toPx()
    }

    val optionTopPx = with(density) {
        64.dp.toPx()
    }

    val optionBottomPx = with(density) {
        114.dp.toPx()
    }

    // Animación de la gota secundaria.
    val progress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(
            durationMillis = 320
        ),
        label = "WaterDropProgress"
    )

    val dropScale = 0.25f + 0.75f * progress
    val dropOffset = 64.dp * progress

    // Animación de selección.
    val selectedAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(
            durationMillis = 150
        ),
        label = "SelectedColor"
    )

    // Mantener callbacks actualizados dentro del gesto.
    val navigateState by rememberUpdatedState(onNavigate)
    val holdState by rememberUpdatedState(onHoldChange)

    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        // PARTÍCULAS CONTINUAS
        // Se dibujan detrás de ambos botones.
        WaterParticles(
            active = expanded,
            modifier = Modifier.align(Alignment.Center)
        )

        // GOTA SECUNDARIA
        // Aparece desde el botón original.
        Surface(
            modifier = Modifier
                .offset(y = dropOffset)
                .size(50.dp)
                .graphicsLayer {
                    scaleX = dropScale
                    scaleY = dropScale
                    alpha = progress
                },
            shape = CircleShape,
            color = Color(0xFFDAE4EA).copy(
                alpha = 0.25f + 0.75f * selectedAlpha
            ),
            border = BorderStroke(
                width = 1.dp,
                brush = GlassBorder
            ),
            shadowElevation = if (selected) {
                6.dp
            } else {
                0.dp
            }
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = Res.getUri("files/learn.svg"),
                    contentDescription = "Learn",
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // BOTÓN ORIGINAL DEL DICCIONARIO
        Surface(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .pointerInput(
                    buttonPx,
                    optionTopPx,
                    optionBottomPx,
                    configuration.longPressTimeoutMillis
                ) {
                    awaitEachGesture {
                        val down = awaitFirstDown(
                            requireUnconsumed = false
                        )

                        // Esperamos la pulsación prolongada.
                        val up = withTimeoutOrNull(
                            configuration.longPressTimeoutMillis
                        ) {
                            waitForUpOrCancellation()
                        }

                        if (up != null) {
                            // Toque normal.
                            navigateState("dictionary")
                        } else {

                            // Si aún está presionado,
                            // activar la gota y partículas.
                            expanded = true
                            selected = false
                            holdState(true)

                            var fingerDown = true
                            var activateOption = false

                            try {
                                while (fingerDown) {
                                    val event = awaitPointerEvent()

                                    val change = event.changes
                                        .firstOrNull {
                                            it.id == down.id
                                        }

                                    if (change == null) {
                                        fingerDown = false
                                    } else {
                                        val pos = change.position

                                        selected =
                                            pos.x >= 0f &&
                                                    pos.x <= buttonPx &&
                                                    pos.y >= optionTopPx &&
                                                    pos.y <= optionBottomPx

                                        if (!change.pressed) {
                                            fingerDown = false
                                            activateOption = selected
                                        }

                                        change.consume()
                                    }
                                }
                            } finally {
                                // Detiene las nuevas oleadas.
                                expanded = false
                                selected = false
                                holdState(false)
                            }

                            if (activateOption) {
                                navigateState("oracion")
                            }
                        }
                    }
                },
            shape = CircleShape,
            color = if (expanded) {
                Color(0x6672B7EA)
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
                AsyncImage(
                    model = Res.getUri("files/dicionary.svg"),
                    contentDescription = "Diccionario",
                    modifier = Modifier.size(26.dp)
                )
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
            .clickable {
                onClick()
            },
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
                    color = Color(0xFF333333),
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
fun NavbarPreview() {
    Navbar()
}
