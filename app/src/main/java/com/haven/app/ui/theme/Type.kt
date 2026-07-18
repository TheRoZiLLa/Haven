package com.haven.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * HAVEN Typography Scale — Material 3 Expressive
 * Font: System default (clean sans-serif)
 * Max 2 weights per screen: Regular (400) + Medium/SemiBold (500/600)
 */
val HavenTypography = Typography(
    // Display — hero numbers, large timer displays
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,       // 700
        fontSize   = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp
    ),

    // Title — screen titles, section headers
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize   = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,     // 500
        fontSize   = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    ),

    // Body — primary and secondary content
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,     // 400
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,     // 400
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    // Label — buttons, badges, chips
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,     // 500
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,     // 500
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
)