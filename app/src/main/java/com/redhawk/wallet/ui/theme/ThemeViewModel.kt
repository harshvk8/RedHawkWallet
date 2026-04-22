package com.redhawk.wallet.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {
    val isDarkMode: StateFlow<Boolean> = ThemeManager.isDarkMode

    fun setDarkMode(enabled: Boolean) {
        ThemeManager.setDarkMode(enabled)
    }

    fun toggleTheme() {
        ThemeManager.toggleTheme()
    }
}