package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.UserProfile

class UserRepository(
    private val firestore: FirestoreDataSource
) {
    private fun userPath(uid: String) = "users/$uid"

    suspend fun createUserProfile(uid: String, userProfile: UserProfile) {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        firestore.setDocument(userPath(uid), userProfile)
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getDocument(userPath(uid), UserProfile::class.java)
    }
}