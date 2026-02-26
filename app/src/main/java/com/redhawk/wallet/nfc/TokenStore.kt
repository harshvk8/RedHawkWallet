package com.redhawk.wallet.nfc

import android.content.Context
import android.content.SharedPreferences

class TokenStore(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("token_store", Context.MODE_PRIVATE)

    fun setAvailableTokensJson(json: String) {
        prefs.edit().putString("available_tokens", json).apply()
    }

    fun getAvailableTokensJson(): String {
        return prefs.getString("available_tokens", "[]") ?: "[]"
    }

    fun setUsedTokensJson(json: String) {
        prefs.edit().putString("used_tokens", json).apply()
    }

    fun getUsedTokensJson(): String {
        return prefs.getString("used_tokens", "[]") ?: "[]"
    }

    fun clearTokens() {
        prefs.edit().clear().apply()
    }
}
