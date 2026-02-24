package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.UserProfile

class UserRepository(
    private val firestore: FirestoreDataSource
) {
    // users/{uid}
    private fun userPath(uid: String) = "users/$uid"

    suspend fun createUserProfile(profile: UserProfile) {
        require(profile.uid.isNotBlank()) { "UserProfile.uid cannot be blank" }
        firestore.setDocument(userPath(profile.uid), profile)
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getDocument(userPath(uid), UserProfile::class.java)
    }
}
