package com.hector.koes.components.Calendar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val COLS = 12
private const val ROWS = 5

/**
 * Cuadrícula de actividad estilo "heatmap" de GitHub.
 * Muestra flores en orden secuencial cuando el usuario resuelve palabras en Home.
 * Tonalidades de la flor según palabras resueltas al día:
 *   - 1 a 4 palabras: Rosa
 *   - 5 a 9 palabras: Rosa entre Púrpura
 *   - 10 o más palabras: Púrpura
 */
@Composable
fun HeatCalendar(
    modifier: Modifier = Modifier,
    wordCounts: Map<Int, Int> = emptyMap(),
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.8f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(ROWS) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(COLS) { col ->
                        // Orden de cuadrícula estilo GitHub (columna por columna, de arriba a abajo)
                        val index = col * ROWS + row
                        val count = wordCounts[index] ?: 0

                        HeatCell(
                            modifier = Modifier.weight(1f),
                            wordCount = count,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatCell(
    modifier: Modifier = Modifier,
    wordCount: Int,
) {
    val isActive = wordCount > 0

    Box(
        modifier = modifier
            .aspectRatio(23f / 27f)
            .clip(RoundedCornerShape(3.dp))
            .background(if (isActive) Color.Transparent else Color(0xFFB3D2DC))
            .semantics {
                contentDescription = if (isActive) "Día con $wordCount palabras resueltas" else "Día sin actividad"
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isActive) {
            key(wordCount) {
                AnimatedFlower(wordCount = wordCount)
            }
        }
    }
}

@Composable
private fun AnimatedFlower(wordCount: Int) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(-40f) }
    val alpha = remember { Animatable(0f) }
    val easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    val inPreview = LocalInspectionMode.current

    LaunchedEffect(Unit) {
        if (inPreview) {
            scale.snapTo(1f)
            rotation.snapTo(0f)
            alpha.snapTo(1f)
            return@LaunchedEffect
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 450
                    0f at 0
                    1.15f at 270 using easing
                    1f at 450
                },
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 450
                    -40f at 0
                    6f at 270 using easing
                    0f at 450
                },
            )
        }
        launch {
            alpha.animateTo(1f, animationSpec = tween(270))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(0.85f)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                rotationZ = rotation.value
                this.alpha = alpha.value
            },
    ) {
        Flower(
            modifier = Modifier.fillMaxSize(),
            wordCount = wordCount
        )
    }
}

@Composable
private fun Flower(
    modifier: Modifier = Modifier,
    wordCount: Int = 1
) {
    // Selección de tonalidad según cantidad de palabras resueltas en el día
    val (petalColor, petalColorAlt, centerColor) = when {
        wordCount >= 10 -> Triple(Color(0xFF7A287A), Color(0xFF6B1863), Color(0xFFF7CFE1)) // Púrpura
        wordCount >= 5  -> Triple(Color(0xFFC058A8), Color(0xFFA84893), Color(0xFFF7CFE1)) // Rosa entre Púrpura
        else            -> Triple(Color(0xFFF7CFE1), Color(0xFFE882AD), Color(0xFFC75E5B)) // Rosa (1 palabra)
    }

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val petalRadius = size.minDimension * 0.28f
        val petalDistance = size.minDimension * 0.26f

        val angles = listOf(-90.0, -18.0, 54.0, 126.0, 198.0)
        angles.forEachIndexed { i, angleDeg ->
            val angleRad = angleDeg * PI / 180.0
            val px = cx + (petalDistance * cos(angleRad)).toFloat()
            val py = cy + (petalDistance * sin(angleRad)).toFloat()
            drawCircle(
                color = if (i % 2 == 0) petalColor else petalColorAlt,
                radius = petalRadius,
                center = Offset(px, py),
            )
        }
        drawCircle(
            color = centerColor,
            radius = size.minDimension * 0.16f,
            center = Offset(cx, cy),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeatCalendarPreview() {
    HeatCalendar(
        wordCounts = mapOf(0 to 1, 1 to 5, 2 to 10)
    )
}

@Preview(showBackground = true)
@Composable
private fun FlowerOnlyPreview() {
    Flower(
        modifier = Modifier.size(40.dp),
        wordCount = 10
    )
}
