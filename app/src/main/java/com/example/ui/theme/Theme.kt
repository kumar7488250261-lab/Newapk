package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
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
    secondary = RailwayLightBlue,
    onSecondary = Color.White,
    tertiary = RailwayAmber,
    onTertiary = Color.Black,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = Color.White,
    onSurface = TextPrimary,
    surfaceVariant = LightSurface,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = RailwayRed,
    onError = Color.White
)

@Composable
fun KharsiaLobbyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = RailwayNavy.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
