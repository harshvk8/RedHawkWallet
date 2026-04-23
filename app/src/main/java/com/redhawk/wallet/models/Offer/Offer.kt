package com.redhawk.wallet.data.models

data class Offer(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val discountPercent: Int = 0,
    val studentOnly: Boolean = false,
    val active: Boolean = true
)