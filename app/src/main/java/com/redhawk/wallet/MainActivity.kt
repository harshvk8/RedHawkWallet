package com.redhawk.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.redhawk.wallet.ui.screens.AppNav
import com.redhawk.wallet.ui.theme.RedHawkWalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RedHawkWalletTheme {
                AppNav()
            }
        }
    }
}