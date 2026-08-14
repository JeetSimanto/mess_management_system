package com.messmanager.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.White,
    primaryContainer = DarkPrimaryGlow,
    onPrimaryContainer = Color.White,
    secondary = DarkSecondary,
    onSecondary = Color.White,
    secondaryContainer = DarkSecondaryMuted,
    onSecondaryContainer = Color.White,
    tertiary = DarkTertiary,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceHigh,
    onSurfaceVariant = Color.White,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

// Clean Carbon Mint theme as primary design scheme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00A876),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7EF),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    tertiary = Color(0xFF4F46E5),
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceHigh,
    onSurfaceVariant = Color.White,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MessManagementTheme(
    darkTheme: Boolean = true, // Dark theme is default for Carbon Mint
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
