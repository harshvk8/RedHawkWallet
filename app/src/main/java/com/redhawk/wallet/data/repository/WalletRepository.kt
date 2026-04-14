package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.Transaction
import com.redhawk.wallet.data.models.Wallet

class WalletRepository(
    private val firestore: FirestoreDataSource
) {

    private fun walletPath(uid: String) = "users/$uid/wallet/main"
    private fun transactionsPath(uid: String) = "users/$uid/transactions"
    private fun transactionPath(uid: String, transactionId: String) =
        "${transactionsPath(uid)}/$transactionId"

    // GET WALLET
    suspend fun getWallet(uid: String): Wallet? {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getDocument(walletPath(uid), Wallet::class.java)
    }

    // CREATE WALLET (THIS FIXES initWallet ERROR)
    suspend fun initWallet(uid: String) {
        require(uid.isNotBlank()) { "uid cannot be blank" }

        val wallet = Wallet(
            id = "main",
            balance = 100.0,   // default balance
            points = 0
        )

        firestore.setDocument(walletPath(uid), wallet)
    }

    // UPDATE BALANCE
    suspend fun updateBalance(uid: String, balance: Double) {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        firestore.updateDocument(
            walletPath(uid),
            mapOf("balance" to balance)
        )
    }

    // GET TRANSACTIONS
    suspend fun getTransactions(uid: String): List<Transaction> {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getCollection(
            transactionsPath(uid),
            Transaction::class.java
        )
    }

    // ADD TRANSACTION
    suspend fun addTransaction(uid: String, transactionId: String, transaction: Transaction) {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        firestore.setDocument(transactionPath(uid, transactionId), transaction)
    }

    // NFC PAYMENT (THIS FIXES tapAndPayWithToken ERROR)
    suspend fun tapAndPayWithToken(uid: String, token: String) {
        require(uid.isNotBlank()) { "uid cannot be blank" }

        val wallet = getWallet(uid) ?: return

        val newBalance = wallet.balance - 5.0

        updateBalance(uid, newBalance)

        val transaction = Transaction(
            id = token,
            title = "NFC Payment",
            amount = -5.0,
            type = "debit",
            timestamp = System.currentTimeMillis()
        )

        addTransaction(uid, token, transaction)
    }
}