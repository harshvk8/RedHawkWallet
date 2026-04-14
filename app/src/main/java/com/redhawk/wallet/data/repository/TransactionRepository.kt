package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.Transaction

class TransactionRepository(
    private val firestore: FirestoreDataSource
) {

    private fun transactionsPath(uid: String) = "users/$uid/transactions"

    private fun transactionPath(uid: String, transactionId: String) =
        "${transactionsPath(uid)}/$transactionId"

    suspend fun getTransactions(uid: String): List<Transaction> {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getCollection(
            transactionsPath(uid),
            Transaction::class.java
        )
    }

    suspend fun getTransactionHistory(uid: String): List<Transaction> {
        return getTransactions(uid)
    }

    suspend fun addTransaction(uid: String, transactionId: String, transaction: Transaction) {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        require(transactionId.isNotBlank()) { "transactionId cannot be blank" }
        firestore.setDocument(transactionPath(uid, transactionId), transaction)
    }

    suspend fun addTransaction(uid: String, transaction: Transaction) {
        require(uid.isNotBlank()) { "uid cannot be blank" }

        val id = if (transaction.id.isNotBlank()) {
            transaction.id
        } else {
            System.currentTimeMillis().toString()
        }

        firestore.setDocument(
            transactionPath(uid, id),
            transaction.copy(id = id)
        )
    }

    suspend fun createTransaction(uid: String, transaction: Transaction) {
        addTransaction(uid, transaction)
    }
}