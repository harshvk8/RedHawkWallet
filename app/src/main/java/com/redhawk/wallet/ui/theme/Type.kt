package com.redhawk.wallet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Uses system default serif-style display + clean sans body.
// To use a custom font, drop the .ttf into res/font/ and reference it here.
val AppTypography = Typography(

    // Large headers — e.g. screen titles
    headlineLarge = TextStyle(
        fontSize   = 30.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    ),

    // Card titles, section headers
    headlineMedium = TextStyle(
        fontSize   = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 30.sp,
        letterSpacing = (-0.3).sp
    ),

    // Sub-screen titles
    headlineSmall = TextStyle(
        fontSize   = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp
    ),

    // List item titles, card labels
    titleLarge = TextStyle(
        fontSize   = 18.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp
    ),

    titleMedium = TextStyle(
        fontSize      = 15.sp,
        fontWeight    = FontWeight.SemiBold,
        letterSpacing = 0.1.sp
    ),

    titleSmall = TextStyle(
        fontSize      = 13.sp,
        fontWeight    = FontWeight.Medium,
        letterSpacing = 0.1.sp
    ),

    // Body copy
    bodyLarge = TextStyle(
        fontSize   = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    ),

    bodyMedium = TextStyle(
        fontSize   = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 21.sp
    ),

    bodySmall = TextStyle(
        fontSize   = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp
    ),

    // Buttons, chips, badges
    labelLarge = TextStyle(
        fontSize      = 14.sp,
        fontWeight    = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    ),

    labelMedium = TextStyle(
        fontSize      = 12.sp,
        fontWeight    = FontWeight.Medium,
        letterSpacing = 0.4.sp
    ),

    labelSmall = TextStyle(
        fontSize      = 10.sp,
        fontWeight    = FontWeight.Medium,
        letterSpacing = 1.sp
    )
)