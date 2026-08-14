package com.messmanager.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark Theme Colors (Carbon Mint Palette)
val DarkPrimary = Color(0xFF00E5A0)
val DarkPrimaryGlow = Color(0x5500E5A0)
val DarkPrimaryMuted = Color(0xFF00B37D)

val DarkSecondary = Color(0xFFFFB547)
val DarkSecondaryMuted = Color(0xFFCC9139)
val DarkSecondaryGlow = Color(0x55FFB547)

val DarkTertiary = Color(0xFF818CF8)
val DarkTertiaryGlow = Color(0x55818CF8)

val DarkBackground = Color(0xFF0A0E14)
val DarkSurfaceLowest = Color(0xFF0F1318)
val DarkSurface = Color(0xFF141B22)
val DarkSurfaceHigh = Color(0xFF1C252E)
val DarkSurfaceBright = Color(0xFF253240)

val DarkOnBackground = Color(0xFFFFFFFF)
val DarkOnSurface = Color(0xFFFFFFFF)
val DarkOnSurfaceDim = Color(0xFFFFFFFF)
val DarkOnSurfaceFaint = Color(0xFFFFFFFF)

val DarkOutline = Color(0xFF2A3544)
val DarkOutlineVariant = Color(0xFF1E2836)

// Semantic Colors (Denser Green & Yellow Fields)
val PositiveDark = Color(0xFF00E5A0)
val PositiveDarkBg = Color(0x4000E5A0)
val PositiveLight = Color(0xFF059669)

val NegativeDark = Color(0xFFF87171)
val NegativeDarkBg = Color(0x40F87171)

val InfoDark = Color(0xFF60A5FA)
val InfoDarkBg = Color(0x4060A5FA)

val WarningDark = Color(0xFFFFB547)
val WarningDarkBg = Color(0x40FFB547)

// Gradient Brushes
val PrimaryHeroGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF00E5A0).copy(alpha = 0.45f),
        Color(0xFF00B37D).copy(alpha = 0.25f)
    )
)

val CardGlowGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1C252E),
        Color(0xFF141B22)
    )
)

val AccentCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x33818CF8),
        Color(0x11818CF8)
    )
)

// Avatar Color Palette
val AvatarColors = listOf(
    Color(0xFF00E5A0),
    Color(0xFFFFB547),
    Color(0xFF818CF8),
    Color(0xFFF472B6),
    Color(0xFF38BDF8),
    Color(0xFFA78BFA),
    Color(0xFFFB923C)
)
