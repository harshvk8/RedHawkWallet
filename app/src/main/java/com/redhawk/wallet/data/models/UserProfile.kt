package com.redhawk.wallet.data.models

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val universityId: String = "",
    val photoUrl: String? = null,
    val role: String = "student",
    val isEmailVerified: Boolean = false,
    val createdAt: Long = 0L
)