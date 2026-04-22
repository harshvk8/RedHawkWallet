package com.redhawk.wallet.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.redhawk.wallet.data.models.Transactions
import com.redhawk.wallet.data.models.UserProfile
import com.redhawk.wallet.data.models.Wallet
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // -----------------------------
    // USER
    // -----------------------------
    suspend fun createUserProfile(uid: String, profile: UserProfile) {
        db.collection("users").document(uid).set(profile).await()
    }

    suspend fun setDocument(documentPath: String, data: Any) {
        db.document(documentPath).set(data).await()
    }

    suspend fun <T> getDocument(documentPath: String, clazz: Class<T>): T? {
        val snap = db.document(documentPath).get().await()
        return snap.toObject(clazz)
    }

    // -----------------------------
    // WALLET
    // -----------------------------
    suspend fun initWallet(uid: String, initialBalance: Double = 200.0) {
        val wallet = Wallet(
            uid = uid,
            balance = initialBalance,
            updatedAt = System.currentTimeMillis()
        )
        db.collection("wallets").document(uid).set(wallet).await()
    }

    suspend fun getWallet(uid: String): Wallet? {
        val snap = db.collection("wallets").document(uid).get().await()
        return snap.toObject(Wallet::class.java)
    }
    suspend fun updateDocument(documentPath: String, data: Map<String, Any>) {
        db.document(documentPath).update(data).await()
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
                "balance" to newBalance,
                "updatedAt" to System.currentTimeMillis()
            ))

            val tx = com.redhawk.wallet.data.models.Transactions(
                id = txId,
                uid = uid,
                token = token,                 // ✅ NFC token saved
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

    // -----------------------------
    // TAP TO PAY (Atomic Deduct + Transaction Save)
    // -----------------------------


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
                "balance" to newBalance,
                "updatedAt" to System.currentTimeMillis()
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

    // -----------------------------
    // TRANSACTIONS
    // -----------------------------
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

    suspend fun addDocument(collectionPath: String, data: Any): String {
        val ref = db.collection(collectionPath).add(data).await()
        return ref.id
    }

    suspend fun <T> getCollection(
        collectionPath: String,
        clazz: Class<T>
    ): List<T> {
        val snap = db.collection(collectionPath).get().await()
        return snap.toObjects(clazz)
    }
}