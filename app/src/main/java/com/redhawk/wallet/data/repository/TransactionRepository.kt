package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.Transactions

class TransactionRepository(
    private val firestore: FirestoreDataSource
) {

    private fun transactionsPath(uid: String) = "users/$uid/transactions"

    private fun transactionPath(uid: String, transactionId: String) =
        "${transactionsPath(uid)}/$transactionId"

    suspend fun getTransactions(uid: String): List<Transactions> {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getCollection(
            transactionsPath(uid),
            Transactions::class.java
        )
    }

    suspend fun getTransactionHistory(uid: String): List<Transactions> {
        return getTransactions(uid)
    }

    suspend fun addTransaction(uid: String, transactionId: String, transaction: Transactions) {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        require(transactionId.isNotBlank()) { "transactionId cannot be blank" }

        val finalTx = transaction.copy(id = transactionId, uid = uid)
        firestore.setDocument(transactionPath(uid, transactionId), finalTx)
    }

    suspend fun addTransaction(uid: String, transaction: Transactions) {
        require(uid.isNotBlank()) { "uid cannot be blank" }

        val txId = if (transaction.id.isNotBlank()) {
            transaction.id
        } else {
            System.currentTimeMillis().toString()
        }

        val finalTx = transaction.copy(id = txId, uid = uid)
        firestore.setDocument(transactionPath(uid, txId), finalTx)
    }

    suspend fun createTransaction(uid: String, transaction: Transactions) {
        addTransaction(uid, transaction)
    }
}