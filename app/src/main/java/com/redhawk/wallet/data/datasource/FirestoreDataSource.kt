package com.redhawk.wallet.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.redhawk.wallet.data.models.UserProfile
import com.redhawk.wallet.data.models.Wallet
import kotlinx.coroutines.tasks.await

class FirestoreDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun createUserProfile(uid: String, profile: UserProfile) {
        db.collection("users")
            .document(uid)
            .set(profile)
            .await()
    }

    suspend fun createWallet(uid: String) {
        val wallet = Wallet(
            uid = uid,
            balance = 0.0,
            updatedAt = System.currentTimeMillis()
        )

        db.collection("wallets")
            .document(uid)
            .set(wallet)
            .await()
    }
}