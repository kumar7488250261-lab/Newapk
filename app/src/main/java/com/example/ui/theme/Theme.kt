package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = RailwayBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = RailwayBlue,
    secondary = RailwayGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = RailwayGold,
    tertiary = RailwaySuccess,
    background = RailwaySurface,
    onBackground = RailwayTextPrimary,
    surface = RailwayCardBg,
    onSurface = RailwayTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = RailwayTextSecondary,
    outline = RailwayDivider
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
