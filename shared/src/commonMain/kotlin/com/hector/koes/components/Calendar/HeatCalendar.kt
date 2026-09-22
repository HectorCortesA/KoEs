package com.hector.koes.components.Calendar

// Dependencias esperadas (Compose Multiplatform "normal"):
//   implementation(compose.foundation)
//   implementation(compose.material3)
//   implementation(compose.animation)
//   implementation(compose.ui)
//   implementation(compose.components.resources) // si usas SVG como recurso, ver nota al final
//
// No requiere nada específico de Android: funciona igual en el módulo commonMain
// de un proyecto Kotlin Multiplatform abierto en Android Studio.
//
// OJO CON @Preview:
// androidx.compose.ui.tooling.preview.Preview es un artefacto SOLO de Android.
// Si este archivo vive en commonMain, ese import no va a resolver ahí.
//   - Si el archivo está en androidMain -> deja el import de androidx tal cual.
//   - Si está en commonMain -> usa org.jetbrains.compose.ui.tooling.preview.Preview
//     (requiere el módulo de "Compose Multiplatform Preview", disponible desde
//     Compose Multiplatform 1.6+). El código de HeatCalendarPreview no cambia,
//     solo el import.

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val COLS = 12
private const val ROWS = 5
private val DEFAULT_SEED = setOf(0, 13, 20, 27, 34, 41, 5, 18, 31, 44, 52, 9)

/**
 * Cuadrícula de actividad estilo "heatmap" (como GitHub contributions).
 * Cada celda alterna entre inactiva (cuadro celeste) y activa (flor animada).
 *
 * Equivalente en Compose Multiplatform del componente React original.
 */
@Composable
fun HeatCalendar(
    modifier: Modifier = Modifier,
    initialActive: Set<Int> = DEFAULT_SEED,
) {
    var active by remember { mutableStateOf(initialActive) }

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
                        val index = row * COLS + col
                        val isActive = active.contains(index)
                        HeatCell(
                            modifier = Modifier.weight(1f),
                            isActive = isActive,
                            onToggle = {
                                active = if (isActive) active - index else active + index
                            },
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
    isActive: Boolean,
    onToggle: () -> Unit,
) {
    Box(
        modifier = modifier
            // Proporción original (23dp x 27dp) conservada, pero el tamaño
            // real ahora depende del ancho disponible (weight(1f) en el Row
            // padre) en vez de un dp fijo -> se ve igual en cualquier pantalla.
            .aspectRatio(23f / 27f)
            .clip(RoundedCornerShape(3.dp))
            .background(if (isActive) Color.Transparent else Color(0xFFB3D2DC))
            .clickable(onClick = onToggle)
            .semantics {
                contentDescription = if (isActive) "Día con actividad" else "Día sin actividad"
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isActive) {
            // key() fuerza que la animación se relance cada vez que la celda
            // pasa de inactiva a activa (equivalente al "pop" del CSS original).
            key(isActive) {
                AnimatedFlower()
            }
        }
    }
}

/** Envuelve [Flower] con la animación de aparición (scale + rotate + fade). */
@Composable
private fun AnimatedFlower() {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(-40f) }
    val alpha = remember { Animatable(0f) }
    val easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    val inPreview = LocalInspectionMode.current

    LaunchedEffect(Unit) {
        if (inPreview) {
            // Los @Preview estáticos de Android Studio no avanzan corrutinas:
            // se congelan en el primer frame de la animación (scale=0,
            // alpha=0), por eso ahí la flor se ve "invisible". En preview
            // saltamos directo al estado final para poder revisar el diseño.
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
        Flower(modifier = Modifier.fillMaxSize())
    }
}

/**
 * Ícono de flor — PLACEHOLDER dibujado con Canvas usando la paleta de colores
 * del diseño original (#F7CFE1, #C75E5B, #D47675, #D87975, #D57877, #C25653).
 *
 * No tengo el contenido real de "imports/EstadoDeCalor/svg-2c6zhros7z" (los
 * paths exactos del SVG), así que esto es una aproximación visual, no un
 * calco pixel-perfect. Dos formas de reemplazarlo por el ícono real:
 *
 * 1) Recurso multiplataforma (recomendado):
 *    - Copia el archivo .svg original a
 *      composeApp/src/commonMain/composeResources/drawable/flower.svg
 *    - Reemplaza el cuerpo de esta función por:
 *          Image(
 *              painter = painterResource(Res.drawable.flower),
 *              contentDescription = null,
 *              modifier = Modifier.size(20.dp),
 *          )
 *      (requiere el plugin de Compose Multiplatform Resources y el import
 *      generado `Res` de tu módulo).
 *
 * 2) Si me pasas el contenido de svg-2c6zhros7z (los strings "d" de cada
 *    <path>), te devuelvo esta función construyendo un androidx.compose.ui.graphics.Path
 *    real con esos datos, igual de fiel que el original en React.
 */
@Composable
private fun Flower(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val petalColor = Color(0xFFD47675)
        val petalColorAlt = Color(0xFFC75E5B)
        val centerColor = Color(0xFFF7CFE1)
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
    HeatCalendar()
}

/** Preview aislado del ícono, sin animación, para verificar que la forma y colores estén bien. */
@Preview(showBackground = true)
@Composable
private fun FlowerOnlyPreview() {
    Flower(modifier = Modifier.size(40.dp))
}