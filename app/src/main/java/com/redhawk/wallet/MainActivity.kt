package com.redhawk.wallet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.repository.WalletRepository
import com.redhawk.wallet.feature_auth.AuthViewModel
import com.redhawk.wallet.nfc.NfcManager
import com.redhawk.wallet.nfc.NfcRepository
import com.redhawk.wallet.nfc.NfcResult
import com.redhawk.wallet.ui.navigation.AppNav
import com.redhawk.wallet.ui.theme.RedHawkWalletTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var nfcManager: NfcManager? = null
    private val walletRepo by lazy { WalletRepository(FirestoreDataSource()) }

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d("APP_START", "MainActivity onCreate")

        // Initialize NFC safely so launch never dies because of NFC
        try {
            nfcManager = NfcManager(this)
            Log.d("APP_START", "NfcManager initialized")
        } catch (e: Exception) {
            nfcManager = null
            Log.e("APP_START", "Failed to initialize NfcManager: ${e.message}", e)
        }

        // Seed tokens safely and non-fatally
        seedTokensIfUserExists()

        setContent {
            RedHawkWalletTheme {
                val navController = rememberNavController()
                AppNav(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }

    private fun seedTokensIfUserExists() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Log.d("APP_START", "No logged-in user, skipping token seed")
            return
        }

        lifecycleScope.launch {
            try {
                val repo = NfcRepository(this@MainActivity)
                repo.fetchOfflineTokens(
                    userId = currentUser.uid,
                    count = 5,
                    amountCents = 200
                )
                Log.d("APP_START", "Tokens seeded for uid=${currentUser.uid}")
            } catch (e: Exception) {
                Log.e("APP_START", "Token seed failed (non-fatal): ${e.message}", e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            nfcManager?.enableForegroundDispatch(this)
            Log.d("APP_START", "Foreground dispatch enabled")
        } catch (e: Exception) {
            Log.e("APP_START", "Failed to enable foreground dispatch: ${e.message}", e)
        }
    }

    override fun onPause() {
        try {
            nfcManager?.disableForegroundDispatch(this)
            Log.d("APP_START", "Foreground dispatch disabled")
        } catch (e: Exception) {
            Log.e("APP_START", "Failed to disable foreground dispatch: ${e.message}", e)
        }
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val manager = nfcManager ?: run {
            Log.e("NFC", "NfcManager is null, ignoring NFC intent")
            return
        }

        val result = try {
            manager.handleIntent(intent)
        } catch (e: Exception) {
            Log.e("NFC", "handleIntent failed: ${e.message}", e)
            return
        }

        when (result) {
            is NfcResult.Success -> {
                val nfcToken = result.token
                Log.d("NFC", "Token received: $nfcToken")

                lifecycleScope.launch {
                    try {
                        val user = FirebaseAuth.getInstance().currentUser
                        val uid = user?.uid.orEmpty()

                        if (uid.isBlank()) {
                            Log.e("NFC", "No logged-in user. Please login first.")
                            return@launch
                        }

                        val existing = walletRepo.getWallet(uid)
                        if (existing == null) {
                            walletRepo.initWallet(uid)
                        }

                        walletRepo.tapAndPayWithToken(uid, nfcToken)
                        Log.d("NFC", "Payment success: -$5, token saved: $nfcToken")
                    } catch (e: Exception) {
                        Log.e("NFC", "Payment failed: ${e.message}", e)
                    }
                }
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