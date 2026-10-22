package com.hector.koes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KoesLightColorScheme = lightColorScheme(
    primary = ButtonActiveBlue,
    secondary = ButtonAccentBlue,
    background = Background,
    surface = CardBackground,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun KoesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KoesLightColorScheme,
        content = content
    )
}
