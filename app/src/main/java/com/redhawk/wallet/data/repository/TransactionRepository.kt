package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.Transaction

class TransactionRepository {
    private val dataSource = FirestoreDataSource()

    suspend fun addTransaction(uid: String, transaction: Transaction): String? {
        return dataSource.addDocument("transactions/$uid/items", transaction.toMap())
    }

    suspend fun getTransactions(uid: String): List<Transaction> {
        val querySnapshot = dataSource.getCollection("transactions/$uid/items")
        return if (querySnapshot != null && !querySnapshot.isEmpty) {
            querySnapshot.documents.mapNotNull { doc ->
                val data = doc.data
                if (data != null) {
                    Transaction.fromMap(data)
                } else {
                    null
                }
            }
        } else {
            emptyList()
        }
    }

    suspend fun getTransaction(uid: String, transactionId: String): Transaction? {
        val doc = dataSource.getDocument("transactions/$uid/items/$transactionId")
        return if (doc != null && doc.exists()) {
            val data = doc.data
            if (data != null) {
                Transaction.fromMap(data)
            } else {
                null
            }
        } else {
            null
        }
    }
}
