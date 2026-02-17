package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.model.Wallet

class WalletRepository(
    private val firestore: FirestoreDataSource
) {
    // wallets/{uid}
    private fun walletPath(uid: String) = "wallets/$uid"

    suspend fun getWallet(uid: String): Wallet? {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getDocument(walletPath(uid), Wallet::class.java)
    }

    suspend fun updateWalletBalance(uid: String, newBalance: Double) {
        require(uid.isNotBlank()) { "uid cannot be blank" }

        firestore.updateFields(
            path = walletPath(uid),
            fields = mapOf(
                "balance" to newBalance,
                "updatedAt" to System.currentTimeMillis()
            )
        )
    }
}
