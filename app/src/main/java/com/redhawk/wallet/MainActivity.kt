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

    private lateinit var nfcManager: NfcManager
    private val walletRepo by lazy { WalletRepository(FirestoreDataSource()) }

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d("NFC_TEST", "MainActivity started")

        nfcManager = NfcManager(this)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val repo = NfcRepository(this)
            lifecycleScope.launch {
                try {
                    repo.fetchOfflineTokens(
                        userId = currentUser.uid,
                        count = 5,
                        amountCents = 200
                    )
                    Log.d("NFC_TEST", "Tokens seeded for uid=${currentUser.uid}")
                } catch (e: Exception) {
                    Log.e("NFC_TEST", "Token seed failed (non-fatal): ${e.message}")
                }
            }
        } else {
            Log.d("NFC_TEST", "No logged-in user — skipping token seed until after login")
        }

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
                        if (existing == null) walletRepo.initWallet(uid)

                        walletRepo.tapAndPayWithToken(uid, nfcToken)
                        Log.d("NFC", "Payment success: -$5, token saved: $nfcToken")

                    } catch (e: Exception) {
                        Log.e("NFC", "Payment failed: ${e.message}", e)
                    }
                }
            }

            is NfcResult.Error -> Log.e("NFC", "Error: ${result.message}")
            NfcResult.Disabled -> Log.e("NFC", "NFC disabled")
            NfcResult.NotSupported -> Log.e("NFC", "NFC not supported")
        }
    }
}