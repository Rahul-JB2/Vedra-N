package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VedraPurplePrimary,
    secondary = VedraCyanAccent,
    tertiary = VedraPinkAccent,
    background = VedraBackground,
    surface = VedraSurface,
    surfaceVariant = VedraSurfaceVariant,
    onPrimary = VedraTextPrimary,
    onSecondary = VedraBackground,
    onBackground = VedraTextPrimary,
    onSurface = VedraTextPrimary,
    onSurfaceVariant = VedraTextSecondary,
    outline = VedraBorder
)

@Composable
fun VedraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

