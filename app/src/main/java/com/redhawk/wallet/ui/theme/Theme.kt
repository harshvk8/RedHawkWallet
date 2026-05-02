package com.redhawk.wallet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary            = RedHawkRed,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFFFDADA),
    onPrimaryContainer = RedHawkRedDark,

    secondary            = RedHawkGold,
    onSecondary          = TextPrimary,
    secondaryContainer   = Color(0xFFFFF3D6),
    onSecondaryContainer = Color(0xFF4A3800),

    background        = BackgroundLight,
    onBackground      = TextPrimary,

    surface           = SurfaceLight,
    onSurface         = TextPrimary,
    surfaceVariant    = SurfaceVariantL,
    onSurfaceVariant  = TextSecondary,

    outline           = Color(0xFFCCC8C3),
    outlineVariant    = Color(0xFFE2DDD8),

    error             = ErrorRed,
    onError           = Color.White,
)

private val DarkColors = darkColorScheme(
    primary            = RedHawkRedLight,
    onPrimary          = Color.White,
    primaryContainer   = RedHawkRedDark,
    onPrimaryContainer = Color(0xFFFFDADA),

    secondary            = RedHawkGold,
    onSecondary          = Color(0xFF1A1200),
    secondaryContainer   = Color(0xFF3D3000),
    onSecondaryContainer = RedHawkGoldLight,

    background        = BackgroundDark,
    onBackground      = OnSurfaceDark,

    surface           = SurfaceDark,
    onSurface         = OnSurfaceDark,
    surfaceVariant    = SurfaceVariantD,
    onSurfaceVariant  = OnSurfaceVarD,

    outline           = Color(0xFF4A4440),
    outlineVariant    = Color(0xFF352F2B),

    error             = ErrorRed,
    onError           = Color.White,
)

@Composable
fun RedHawkWalletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography  = AppTypography,
        content     = content
    )
}