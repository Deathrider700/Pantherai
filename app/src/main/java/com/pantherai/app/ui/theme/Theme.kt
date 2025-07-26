package com.pantherai.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Theme Colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E3A8A), // Deep Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBE2FF),
    onPrimaryContainer = Color(0xFF001849),
    secondary = Color(0xFF7C3AED), // Purple
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9D5FF),
    onSecondaryContainer = Color(0xFF21005D),
    tertiary = Color(0xFF059669), // Emerald
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF00201C),
    error = Color(0xFFDC2626), // Red
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    background = Color(0xFFF8FAFC), // Light Gray
    onBackground = Color(0xFF1E293B),
    surface = Color.White,
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF1E293B),
    inverseOnSurface = Color(0xFFF8FAFC),
    inversePrimary = Color(0xFFB3C8FF),
    surfaceDim = Color(0xFFF1F5F9),
    surfaceBright = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE2E8F0),
    surfaceContainerHighest = Color(0xFFCBD5E1),
)

// Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB3C8FF), // Light Blue
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF23272F),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFD8B4FE), // Light Purple
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF23272F),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFA7F3D0), // Light Emerald
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF23272F),
    onTertiaryContainer = Color.White,
    error = Color(0xFFFCA5A5), // Light Red
    onError = Color.Black,
    errorContainer = Color(0xFF23272F),
    onErrorContainer = Color.White,
    background = Color.Black, // True black
    onBackground = Color.White,
    surface = Color(0xFF111113), // Near black for cards/surfaces
    onSurface = Color.White,
    surfaceVariant = Color(0xFF23272F), // Slightly lighter for variant
    onSurfaceVariant = Color(0xFFBFC9D1),
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF23272F),
    scrim = Color(0xFF000000),
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    inversePrimary = Color(0xFF1E3A8A),
    surfaceDim = Color.Black,
    surfaceBright = Color(0xFF23272F),
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color(0xFF111113),
    surfaceContainer = Color(0xFF23272F),
    surfaceContainerHigh = Color(0xFF23272F),
    surfaceContainerHighest = Color(0xFF23272F),
)

@Composable
fun PantherAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 