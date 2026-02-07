package com.redhawk.wallet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = RedHawkRed,
    secondary = RedHawkGold,
    background = BackgroundLight,
    error = ErrorRed
)

@Composable
fun RedHawkWalletTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
