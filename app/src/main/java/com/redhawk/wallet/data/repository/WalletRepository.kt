package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.Wallet

class WalletRepository {
    private val dataSource = FirestoreDataSource()

    suspend fun getWallet(uid: String): Wallet? {
        val doc = dataSource.getDocument("wallets/$uid")
        return if (doc != null && doc.exists()) {
            val data = doc.data
            if (data != null) {
                Wallet.fromMap(data)
            } else {
                null
            }
        } else {
            null
        }
    }

    suspend fun createWalletIfMissing(uid: String): Boolean {
        val existing = getWallet(uid)
        return if (existing == null) {
            val newWallet = Wallet(uid = uid, balance = 100.0)
            dataSource.setDocument("wallets/$uid", newWallet.toMap())
        } else {
            true
        }
    }

    suspend fun updateWalletBalance(uid: String, newBalance: Double): Boolean {
        return dataSource.updateFields(
            "wallets/$uid",
            mapOf(
                "balance" to newBalance,
                "lastUpdated" to System.currentTimeMillis()
            )
        )
    }
}
