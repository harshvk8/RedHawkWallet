package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.Transactions
import com.redhawk.wallet.data.models.Wallet

class WalletRepository(
    private val ds: FirestoreDataSource
) {
    suspend fun initWallet(uid: String) = ds.initWallet(uid, 200.0)
    suspend fun getWallet(uid: String): Wallet? = ds.getWallet(uid)
    suspend fun tapAndPay(uid: String): Transactions = ds.tapAndPay(uid, 5.0)
    suspend fun getLatestTransactions(uid: String): List<Transactions> = ds.getLatestTransactions(uid)
    suspend fun tapAndPayWithToken(uid: String, token: String): Transactions =
        ds.tapAndPayWithToken(uid, token, 5.0)
}
