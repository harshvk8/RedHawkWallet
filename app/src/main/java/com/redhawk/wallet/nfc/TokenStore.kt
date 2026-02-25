package com.redhawk.wallet.nfc

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenStore(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "redhawk_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setAvailableTokensJson(json: String) {
        prefs.edit().putString(KEY_AVAILABLE_TOKENS, json).apply()
    }

    fun getAvailableTokensJson(): String {
        return prefs.getString(KEY_AVAILABLE_TOKENS, "[]") ?: "[]"
    }

    fun setUsedTokensJson(json: String) {
        prefs.edit().putString(KEY_USED_TOKENS, json).apply()
    }

    fun getUsedTokensJson(): String {
        return prefs.getString(KEY_USED_TOKENS, "[]") ?: "[]"
    }

    companion object {
        private const val KEY_AVAILABLE_TOKENS = "available_tokens_json"
        private const val KEY_USED_TOKENS = "used_tokens_json"
    }
}