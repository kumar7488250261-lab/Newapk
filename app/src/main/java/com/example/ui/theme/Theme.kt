package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = RailwayNavy,
    onPrimary = Color.White,
    primaryContainer = RailwayBlue,
    onPrimaryContainer = Color.White,
    secondary = RailwayAmber,
    onSecondary = Color.Black,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = Color.White,
    onSurface = TextPrimary,
    surfaceVariant = LightSurface,
    onSurfaceVariant = TextSecondary,
    error = RailwayRed,
    onError = Color.White
)

@Composable
fun KharsiaLobbyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = RailwayNavy.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
