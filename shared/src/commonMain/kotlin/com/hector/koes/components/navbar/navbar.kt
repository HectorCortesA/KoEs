
package com.hector.koes.components.navbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hector.koes.resources.Res
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

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

    val isLearnActive = activeOption == "oracion"

    val mainIcon = if (isLearnActive) {
        "files/learn.svg"
    } else {
        "files/dicionary.svg"
    }

    val secondaryIcon = if (isLearnActive) {
        "files/dicionary.svg"
    } else {
        "files/learn.svg"
    }

    val mainRoute = if (isLearnActive) {
        "oracion"
    } else {
        "dictionary"
    }

    val secondaryRoute = if (isLearnActive) {
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
    val topPx = with(density) { 64.dp.toPx() }
    val bottomPx = with(density) { 114.dp.toPx() }

    val progress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(durationMillis = 320),
        label = "WaterDrop"
    )

    val selectionProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 150),
        label = "DropSelection"
    )

    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Partículas radiales continuas.
        WaterParticles(
            active = expanded,
            modifier = Modifier.align(Alignment.Center)
        )

        // Botón secundario: nace detrás del principal.
        Surface(
            modifier = Modifier
                .offset(y = 64.dp * progress)
                .size(50.dp)
                .graphicsLayer {
                    scaleX = 0.25f + 0.75f * progress
                    scaleY = 0.25f + 0.75f * progress
                    alpha = progress
                },
            shape = CircleShape,
            color = Color(0xFFDAE4EA).copy(
                alpha = 0.25f + 0.75f * selectionProgress
            ),
            border = BorderStroke(
                width = 1.dp,
                brush = GlassBorder
            ),
            shadowElevation = if (selected) 6.dp else 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = Res.getUri(secondaryIcon),
                    contentDescription = "Opción secundaria",
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Botón principal.
        Surface(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .pointerInput(
                    buttonPx,
                    topPx,
                    bottomPx,
                    configuration.longPressTimeoutMillis
                ) {
                    awaitEachGesture {
                        val down = awaitFirstDown()

                        var quickTap = false
                        var cancelled = false

                        // Primero distinguimos el toque corto
                        // de una pulsación prolongada.
                        val finishedEarly = withTimeoutOrNull(
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
                                    quickTap = true
                                    finished = true
                                    change.consume()
                                } else if (
                                    change.isConsumed ||
                                    abs(change.position.x - down.position.x) >
                                    viewConfiguration.touchSlop ||
                                    abs(change.position.y - down.position.y) >
                                    viewConfiguration.touchSlop
                                ) {
                                    cancelled = true
                                    finished = true
                                }
                            }
                            true
                        }

                        if (finishedEarly == true) {
                            if (quickTap && !cancelled) {
                                navigateState(mainRouteState)
                            }
                        } else {
                            // Pulsación prolongada.
                            expanded = true
                            selected = false
                            holdState(true)

                            var activateSecondary = false

                            try {
                                var fingerDown = true

                                while (fingerDown) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes
                                        .firstOrNull { it.id == down.id }

                                    if (change == null) {
                                        fingerDown = false
                                    } else {
                                        val pos = change.position

                                        selected =
                                            pos.x >= 0f &&
                                                    pos.x <= buttonPx &&
                                                    pos.y >= topPx &&
                                                    pos.y <= bottomPx

                                        if (!change.pressed) {
                                            activateSecondary = selected
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
                Color(0x6672B7EA)
            } else {
                Color.Transparent
            },
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = Res.getUri(mainIcon),
                    contentDescription = "Opción principal",
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
            .clickable { onClick() },
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            brush = GlassBorder
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
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
