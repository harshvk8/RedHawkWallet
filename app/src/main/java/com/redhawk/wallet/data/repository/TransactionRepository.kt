package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.model.Transaction

class TransactionRepository(
    private val firestore: FirestoreDataSource
) {
    // transactions/{uid}/items
    private fun itemsPath(uid: String) = "transactions/$uid/items"

    suspend fun addTransaction(uid: String, transaction: Transaction): String {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.addDocument(
            collectionPath = itemsPath(uid),
            data = transaction
        )
    }

    suspend fun getTransactions(uid: String): List<Transaction> {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getCollection(
            collectionPath = itemsPath(uid),
            clazz = Transaction::class.java
        )
    }
}
