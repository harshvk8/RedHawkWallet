package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.UserProfile

class UserRepository {
    private val dataSource = FirestoreDataSource()

    suspend fun createUserProfile(uid: String, profile: UserProfile): Boolean {
        return dataSource.setDocument("users/$uid", profile.toMap())
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        val doc = dataSource.getDocument("users/$uid")
        return if (doc != null && doc.exists()) {
            val data = doc.data
            if (data != null) {
                UserProfile.fromMap(data)
            } else {
                null
            }
        } else {
            null
        }
    }

    suspend fun existsEmail(email: String): Boolean {
        return dataSource.checkEmailExists(email)
    }

    suspend fun existsStudentId(studentId: String): Boolean {
        return dataSource.checkStudentIdExists(studentId)
    }

    suspend fun updateUserProfile(uid: String, updates: Map<String, Any?>): Boolean {
        return dataSource.updateFields("users/$uid", updates)
    }
}
