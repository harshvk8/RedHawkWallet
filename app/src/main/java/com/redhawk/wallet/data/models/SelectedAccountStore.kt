package com.redhawk.wallet.data.models

import android.content.Context

class SelectedAccountStore(context: Context) {

    private val prefs = context.getSharedPreferences("selected_account_prefs", Context.MODE_PRIVATE)

    fun save(accountType: AccountType) {
        prefs.edit().putString("selected_account", accountType.name).apply()
    }

    fun get(): AccountType {
        val saved = prefs.getString("selected_account", AccountType.RED_HAWK_DOLLARS.name)
        return try {
            AccountType.valueOf(saved ?: AccountType.RED_HAWK_DOLLARS.name)
        } catch (_: Exception) {
            AccountType.RED_HAWK_DOLLARS
        }
    }
}