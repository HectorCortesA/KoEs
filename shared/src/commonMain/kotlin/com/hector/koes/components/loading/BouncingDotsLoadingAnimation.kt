package com.hector.koes.components.loading

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BouncingDotsLoadingAnimation(
    modifier: Modifier = Modifier,
    dotSize: Dp = 24.dp,
    dotColorActive: Color = Color(0xFF75A5E3),
    dotColorInactive: Color = Color.White,
    jumpHeight: Dp = (-20).dp,
    spaceBetween: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BouncingDotsTransition")

    val dots = listOf(0, 1, 2).map { index ->
        val delay = index * 200

        // 1. ANIMACIÓN DEL SALTO (EJE Y) ajustada a 600ms
        val yOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0f at delay with FastOutSlowInEasing
                    jumpHeight.value at delay + 300 with FastOutSlowInEasing // Sube a los 300ms
                    0f at delay + 600 with FastOutSlowInEasing // Regresa al suelo a los 600ms
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "YOffset-$index"
        )

        // 2. ANIMACIÓN DEL COLOR ajustada a 600ms
        val color by infiniteTransition.animateColor(
            initialValue = dotColorInactive,
            targetValue = dotColorInactive,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    dotColorInactive at delay
                    dotColorActive at delay + 300 // Color azul en el punto máximo del salto
                    dotColorInactive at delay + 600 // Regresa a blanco al tocar el suelo
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "Color-$index"
        )

        Pair(yOffset, color)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spaceBetween),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dots.forEach { (yOffset, color) ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer {
                        translationY = yOffset.dp.toPx()
                    }
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFECECEC)
@Composable
fun Previewload(){
    Box(
        modifier = Modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        BouncingDotsLoadingAnimation()
    }
}