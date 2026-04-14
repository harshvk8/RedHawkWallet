package com.redhawk.wallet.data.models

enum class AccountType(
    val displayName: String
) {
    RED_HAWK_DOLLARS("Red Hawk Dollars / Debit"),
    FLEX("Flex"),
    BONUS("Bonus"),
    MEAL_SWIPES("Meal Swipes")
}