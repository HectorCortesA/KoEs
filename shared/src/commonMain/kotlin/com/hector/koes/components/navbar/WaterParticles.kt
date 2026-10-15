
package com.hector.koes.components.navbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import androidx.compose.runtime.withFrameNanos
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class WaterParticle(
    val angle: Float,
    val speed: Float,
    val radius: Float,
    val lifetime: Float,
    val startRadius: Float,
    val opacity: Float,
    var age: Float = 0f
)

@Composable
fun WaterParticles(
    active: Boolean,
    modifier: Modifier = Modifier
) {
    val particles = remember {
        mutableStateListOf<WaterParticle>()
    }

    val density = LocalDensity.current

    val startRadius = with(density) { 25.dp.toPx() }
    val minSpeed = with(density) { 40.dp.toPx() }
    val maxSpeed = with(density) { 100.dp.toPx() }
    val minRadius = with(density) { 1.5.dp.toPx() }
    val maxRadius = with(density) { 4.dp.toPx() }

    val random = remember { Random.Default }

    // Control de redibujado del Canvas.
    var frameTick by remember {
        mutableIntStateOf(0)
    }

    val maxParticles = 150

    LaunchedEffect(active) {
        var previousFrame = 0L
        var emissionAccumulator = 0f

        // Solo se generan partículas nuevas cuando active = true.
        while (isActive && (active || particles.isNotEmpty())) {

            withFrameNanos { frameTime ->
                val deltaTime = if (previousFrame == 0L) {
                    0f
                } else {
                    ((frameTime - previousFrame) / 1_000_000_000f)
                        .coerceIn(0f, 0.05f)
                }

                previousFrame = frameTime

                // Actualizar las partículas existentes.
                for (i in particles.lastIndex downTo 0) {
                    val particle = particles[i]
                    particle.age += deltaTime

                    if (particle.age >= particle.lifetime) {
                        particles.removeAt(i)
                    }
                }

                if (active) {
                    emissionAccumulator += deltaTime

                    // Una nueva oleada cada 120 ms.
                    if (emissionAccumulator >= 0.12f) {
                        emissionAccumulator -= 0.12f

                        val quantity = random.nextInt(8, 15)

                        // Distribución por toda la circunferencia.
                        val phase = random.nextFloat() *
                                (2f * PI.toFloat())

                        repeat(quantity) { index ->
                            if (particles.size >= maxParticles) {
                                return@repeat
                            }

                            val angle =
                                phase +
                                        (2f * PI.toFloat() * index / quantity) +
                                        (random.nextFloat() - 0.5f) * 0.35f

                            particles.add(
                                WaterParticle(
                                    angle = angle,
                                    speed = minSpeed +
                                            random.nextFloat() *
                                            (maxSpeed - minSpeed),
                                    radius = minRadius +
                                            random.nextFloat() *
                                            (maxRadius - minRadius),
                                    lifetime = 0.55f +
                                            random.nextFloat() * 0.55f,
                                    startRadius = startRadius,
                                    opacity = 0.6f +
                                            random.nextFloat() * 0.4f
                                )
                            )
                        }
                    }
                }

                // Actualiza el dibujo.
                frameTick++
            }
        }
    }

    Canvas(
        modifier = modifier.size(240.dp)
    ) {
        // Mantiene la animación sincronizada con el Canvas.
        frameTick

        val center = Offset(
            x = size.width / 2f,
            y = size.height / 2f
        )

        particles.forEach { particle ->
            val progress = (
                    particle.age / particle.lifetime
                    ).coerceIn(0f, 1f)

            // Expulsión radial, sin rotación.
            val distance =
                particle.startRadius +
                        particle.speed * particle.age

            val x = center.x +
                    cos(particle.angle) * distance

            val y = center.y +
                    sin(particle.angle) * distance

            // Desvanecimiento suave.
            val alpha =
                particle.opacity *
                        (1f - progress) *
                        (1f - progress)

            val currentRadius =
                particle.radius * (1f - progress * 0.55f)

            drawCircle(
                color = Color(0xFF72B7EA).copy(
                    alpha = alpha.coerceIn(0f, 1f)
                ),
                radius = currentRadius,
                center = Offset(x, y)
            )
        }
    }
}
