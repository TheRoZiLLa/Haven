package com.haven.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// HAVEN always uses light theme — warm, nature-forward palette
private val HavenColorScheme = lightColorScheme(
    primary          = ForestGreen,
    onPrimary        = WarmWhite,
    primaryContainer = Mint,
    onPrimaryContainer = ForestGreenDark,

    secondary        = LeafGreen,
    onSecondary      = DarkText,
    secondaryContainer = Mint,
    onSecondaryContainer = ForestGreenDark,

    tertiary         = SoftYellow,
    onTertiary       = DarkText,

    background       = WarmWhite,
    onBackground     = DarkText,

    surface          = LightGray,
    onSurface        = DarkText,
    onSurfaceVariant = MediumGray,

    error            = ErrorCoral,
    onError          = WarmWhite,

    outline          = LeafGreen,
    outlineVariant   = Mint,

    surfaceVariant   = LightGray,
    inverseSurface   = DarkText,
    inverseOnSurface = WarmWhite,
)

@Composable
fun HavenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HavenColorScheme,
        typography  = HavenTypography,
        shapes      = HavenShapes,
        content     = content
    )
}