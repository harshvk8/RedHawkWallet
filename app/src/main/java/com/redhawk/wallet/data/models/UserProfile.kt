package com.redhawk.wallet.data.models

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val studentId: String = "",

    val email: String = "",
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()

)
