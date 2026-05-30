package com.example.codeorbit.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF4B8FE2),
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White,
    outline = DarkBorder,
    onSurfaceVariant = DarkText
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF4B8FE2),
    background = LightBackground,
    surface = LightSurface,
    onBackground = androidx.compose.ui.graphics.Color(0xFF1E293B),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1E293B),
    outline = LightBorder,
    onSurfaceVariant = LightText
)

@Composable
fun CodeOrbitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}