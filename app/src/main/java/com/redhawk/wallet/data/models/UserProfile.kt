package com.redhawk.wallet.data.models

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val studentId: String = "",
    val profilePhotoUrl: String? = null,
    val role: String = "student",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "studentId" to studentId,
            "profilePhotoUrl" to profilePhotoUrl,
            "role" to role,
            "createdAt" to createdAt
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): UserProfile {
            return UserProfile(
                uid = map["uid"] as? String ?: "",
                name = map["name"] as? String ?: "",
                email = map["email"] as? String ?: "",
                studentId = map["studentId"] as? String ?: "",
                profilePhotoUrl = map["profilePhotoUrl"] as? String,
                role = map["role"] as? String ?: "student",
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}
