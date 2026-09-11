package com.hector.koes.components.navbar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Navbar(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit = {}
) {
    Row(
        modifier = modifier
            .safeContentPadding(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        NavButton(text = "Pl", onClick = { onNavigate("dictionary") })
        NavButton(text = "hg", onClick = { onNavigate("hangul") })  // Vista HangulView
        NavButton(text = "LC")
        NavButton(text = "T", onClick = { onNavigate("home") }) // home
        NavButton(text = "PS", ) // Vista

        Spacer(
            modifier = Modifier.width(26.dp)
        )

        NavButton(text = "AJ", onClick = { onNavigate("settings") })
    }
}

@Composable
fun NavButton(
    text: String,
    onClick: () -> Unit = {}
) {
    // Usamos Surface con color transparente para evitar cualquier relleno 
    // y asegurar la forma circular perfecta sin sombras poligonales.
    Surface(
        modifier = Modifier
            .size(50.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.5f),
                    Color.White.copy(alpha = 0.1f)
                )
            )
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 120
)
@Composable
fun NavbarPreview() {
    Navbar(

    )
}