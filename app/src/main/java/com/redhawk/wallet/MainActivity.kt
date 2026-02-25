package com.redhawk.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.redhawk.wallet.ui.screens.AppNav
import com.redhawk.wallet.ui.theme.RedHawkWalletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            RedHawkWalletTheme {
                AppNav()
            }
        }
    }
}