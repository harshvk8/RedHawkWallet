package com.redhawk.wallet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.redhawk.wallet.nfc.NfcManager
import com.redhawk.wallet.nfc.NfcRepository
import com.redhawk.wallet.nfc.NfcResult
import com.redhawk.wallet.ui.navigation.AppNav
import com.redhawk.wallet.ui.theme.RedHawkWalletTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var nfcManager: NfcManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d("NFC_TEST", "MainActivity started")

        // Initialize NFC
        nfcManager = NfcManager(this)

        // OPTIONAL: Seed demo offline tokens
        val repo = NfcRepository(this)
        lifecycleScope.launch {
            repo.fetchOfflineTokens(
                userId = "demoUser123",
                count = 5,
                amountCents = 200
            )
            Log.d("NFC_TEST", "Tokens seeded")
        }

        setContent {
            RedHawkWalletTheme {
                val navController = androidx.navigation.compose.rememberNavController()
                AppNav(navController = navController)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcManager.enableForegroundDispatch(this)
    }

    override fun onPause() {
        super.onPause()
        nfcManager.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val result = nfcManager.handleIntent(intent)

        when (result) {
            is NfcResult.Success -> {
                Log.d("NFC", "Token received: ${result.token}")
                // Later you can navigate to NFC_RESULT screen via shared state
            }

            is NfcResult.Error -> {
                Log.e("NFC", "Error: ${result.message}")
            }

            NfcResult.Disabled -> {
                Log.e("NFC", "NFC disabled")
            }

            NfcResult.NotSupported -> {
                Log.e("NFC", "NFC not supported")
            }
        }
    }
}