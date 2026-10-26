package com.hector.koes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

private val KoesDarkColorScheme = darkColorScheme(
    primary = ButtonActiveBlue,
    secondary = ButtonAccentBlue,
    background = Color(0xFF202020),
    surface = Color(0xFF353535),
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = Color(0xFFF5F5F5),
    onSurface = Color(0xFFF5F5F5)
)

@Composable
fun KoesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KoesDarkColorScheme else KoesLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
