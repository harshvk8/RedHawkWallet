package com.redhawk.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.redhawk.wallet.qr.QrIdScreen
import com.redhawk.wallet.ui.theme.RedHawkWalletTheme

class MainActivity : ComponentActivity() {
    @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RedHawkWalletTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { _ ->
                    QrIdScreen()
                }
            }
        }
    }
}
