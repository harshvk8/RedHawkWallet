package com.redhawk.wallet.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.redhawk.wallet.data.models.Transactions
import kotlinx.coroutines.tasks.await

class TransactionRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun itemsPath(uid: String) = "transactions/$uid/items"

    suspend fun addTransaction(uid: String, transaction: Transactions): String {
        require(uid.isNotBlank()) { "uid cannot be blank" }

        val docRef = firestore
            .collection(itemsPath(uid))
            .add(transaction)
            .await()

        return docRef.id
    }

    suspend fun getTransactions(uid: String): List<Transactions> {
        require(uid.isNotBlank()) { "uid cannot be blank" }

        val snapshot = firestore
            .collection(itemsPath(uid))
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Transactions::class.java)
        }
    }
}