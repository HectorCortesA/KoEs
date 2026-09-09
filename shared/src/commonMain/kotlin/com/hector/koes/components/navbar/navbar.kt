package com.hector.koes.components.navbar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Navbar() {

    Row(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        NavButton( text = "Pl")
        NavButton( text = "hg")
        NavButton( text = "LC")
        NavButton( text = "T")
        NavButton( text = "PS")

        // Separación especial antes del último botón
        Spacer(modifier = Modifier.width(26.dp))

        NavButton( text = "AJ")
    }
}

@Composable
fun NavButton(
    text: String
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            // Fondo con degradado sutil al 20% de opacidad
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x33FFFFFF), // 20% de blanco
                        Color(0x33D9D9D9)  // 20% de tu color base
                    )
                ),
                shape = CircleShape
            )
            // Borde de refracción
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    )
                ),
                shape = CircleShape
            )
            // Elevación y recorte
            .graphicsLayer {
                shadowElevation = 2f
                shape = CircleShape
                clip = true
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF333333)
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 100
)
@Composable
fun NavbarPreview() {
    Navbar()
}