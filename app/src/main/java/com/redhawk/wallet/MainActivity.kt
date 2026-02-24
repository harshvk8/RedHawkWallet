package com.redhawk.wallet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//<<<<<<< feature-ui
import com.redhawk.wallet.ui.screens.AppNav
//=======
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.redhawk.wallet.qr.QrIdScreen
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.redhawk.wallet.nfc.NfcManager
import com.redhawk.wallet.nfc.NfcResult
//>>>>>>> dev
import com.redhawk.wallet.ui.theme.RedHawkWalletTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.redhawk.wallet.nfc.NfcRepository

class MainActivity : ComponentActivity() {
    @Suppress("UnusedMaterial3ScaffoldPaddingParameter")

    private lateinit var nfcManager: NfcManager
    private var nfcStatus by mutableStateOf("NFC: Waiting...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//<<<<<<< feature-ui
        setContent {
            RedHawkWalletTheme {
                AppNav()
            }
        }
    }
}
//=======
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
        Log.e("NFC_TEST", "MainActivity started")

        nfcManager = NfcManager(this)

        nfcStatus = when {
            !nfcManager.isNfcSupported() -> "NFC: Not supported on this device"
            !nfcManager.isNfcEnabled() -> "NFC: Supported but OFF (turn it ON in settings)"
            else -> "NFC: Ready — tap a tag/card"
        }


        // ✅ TEMP TEST: Seed offline tokens (simulate "download tokens when online")
        val repo = NfcRepository(this)

        lifecycleScope.launch {
            repo.fetchOfflineTokens(
                userId = "demoUser123",
                count = 5,
                amountCents = 200
            )
            Log.d("NFC_TEST", "Tokens seeded. Available = ${repo.availableTokenCount()}")
        }

        setContent {
            RedHawkWalletTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        text = nfcStatus,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
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
        Log.d("NFC", "Result: $result")

        nfcStatus = when (result) {
            is NfcResult.Success -> "NFC Token: ${result.token}"
            is NfcResult.Error -> "NFC Error: ${result.message}"
            NfcResult.Disabled -> "NFC is OFF. Turn it ON in settings."
            NfcResult.NotSupported -> "NFC not supported."
        }
    }
}
//>>>>>>> dev
