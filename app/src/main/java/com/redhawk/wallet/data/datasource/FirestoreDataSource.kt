package com.redhawk.wallet.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.redhawk.wallet.data.models.Transactions
import com.redhawk.wallet.data.models.Wallet
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun initWallet(uid: String, initialBalance: Double = 200.0) {
        val wallet = Wallet(
            walletId = uid,
            userId = uid,
            balance = initialBalance
        )
        db.collection("wallets").document(uid).set(wallet).await()
    }

    suspend fun getWallet(uid: String): Wallet? {
        val snap = db.collection("wallets").document(uid).get().await()
        return snap.toObject(Wallet::class.java)
    }

    suspend fun tapAndPayWithToken(
        uid: String,
        token: String,
        amountToDeduct: Double = 5.0
    ): Transactions {

        val walletRef = db.collection("wallets").document(uid)
        val txId = UUID.randomUUID().toString()
        val txRef = db.collection("users").document(uid)
            .collection("transactions").document(txId)

        return db.runTransaction { transaction ->

            val walletSnap = transaction.get(walletRef)
            val currentBalance = walletSnap.getDouble("balance") ?: 0.0

            if (currentBalance < amountToDeduct) {
                throw IllegalStateException("Insufficient balance")
            }

            val newBalance = currentBalance - amountToDeduct

            transaction.update(walletRef, mapOf(
                "balance" to newBalance
            ))

            val tx = Transactions(
                id = txId,
                uid = uid,
                token = token,
                amount = amountToDeduct,
                status = "success",
                type = "nfc_tap",
                note = "Paid via NFC tap",
                timestamp = System.currentTimeMillis()
            )

            transaction.set(txRef, tx)
            tx
        }.await()
    }

    suspend fun tapAndPay(uid: String, amountToDeduct: Double = 5.0): Transactions {

        val walletRef = db.collection("wallets").document(uid)
        val txId = UUID.randomUUID().toString()
        val txRef = db.collection("users")
            .document(uid)
            .collection("transactions")
            .document(txId)

        return db.runTransaction { transaction ->

            val walletSnap = transaction.get(walletRef)
            val currentBalance = walletSnap.getDouble("balance") ?: 0.0

            if (currentBalance < amountToDeduct) {
                throw IllegalStateException("Insufficient balance")
            }

            val newBalance = currentBalance - amountToDeduct

            transaction.update(walletRef, mapOf(
                "balance" to newBalance
            ))

            val token = UUID.randomUUID().toString()

            val tx = Transactions(
                id = txId,
                uid = uid,
                token = token,
                amount = amountToDeduct,
                status = "success",
                type = "tap",
                note = "Demo tap payment",
                timestamp = System.currentTimeMillis()
            )

            transaction.set(txRef, tx)
            tx
        }.await()
    }

    suspend fun getLatestTransactions(
        uid: String,
        limit: Long = 20
    ): List<Transactions> {

        val snap = db.collection("users")
            .document(uid)
            .collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

        return snap.toObjects(Transactions::class.java)
    }
}