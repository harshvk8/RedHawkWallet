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
        "nfc_tokens",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getAvailableTokensJson(): String =
        prefs.getString("available_tokens", "[]") ?: "[]"

    fun setAvailableTokensJson(json: String) {
        prefs.edit().putString("available_tokens", json).apply()
    }

    fun getUsedTokensJson(): String =
        prefs.getString("used_tokens", "[]") ?: "[]"

    fun setUsedTokensJson(json: String) {
        prefs.edit().putString("used_tokens", json).apply()
    }
}
