package com.redhawk.wallet.nfc

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class NfcRepository(private val context: Context) {

    private val tokenStore = TokenStore(context)

    /**
     * STEP 1 (ONLINE):
     * Fetch offline tokens from backend.
     * For now, we simulate this (demo tokens).
     */
    suspend fun fetchOfflineTokens(
        userId: String,
        count: Int,
        amountCents: Int
    ) {
        val tokens = JSONArray()

        repeat(count) { idx ->
            tokens.put(
                JSONObject().apply {
                    put("tokenId", "token_${System.currentTimeMillis()}_$idx")
                    put("userId", userId)
                    put("amountCents", amountCents)
                    put("issuedAt", System.currentTimeMillis())
                    put("expiresAt", System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000) // 7 days
                    put("signature", "MOCK_SIGNATURE")
                }
            )
        }

        tokenStore.setAvailableTokensJson(tokens.toString())
        tokenStore.setUsedTokensJson("[]")
    }

    /**
     * STEP 2 (OFFLINE):
     * Consume ONE token during NFC tap.
     * This is what the card (HCE) sends to terminal.
     */
    fun consumeTokenOfflineJson(): String? {
        val available = JSONArray(tokenStore.getAvailableTokensJson())
        if (available.length() == 0) return null

        val token = available.getJSONObject(0)

        // Remove from available list
        val remaining = JSONArray()
        for (i in 1 until available.length()) {
            remaining.put(available.getJSONObject(i))
        }
        tokenStore.setAvailableTokensJson(remaining.toString())

        // Add to used queue
        val used = JSONArray(tokenStore.getUsedTokensJson())
        used.put(token)
        tokenStore.setUsedTokensJson(used.toString())

        // Build SpendRequest JSON
        val spendRequest = JSONObject().apply {
            put("tokenId", token.getString("tokenId"))
            put("userId", token.getString("userId"))
            put("amountCents", token.getInt("amountCents"))
            put("timestamp", System.currentTimeMillis())
            put("signature", token.getString("signature"))
        }

        return spendRequest.toString()
    }

    /**
     * STEP 3 (ONLINE LATER):
     * Sync used tokens with backend (deduct balance).
     */
    suspend fun syncUsedTokens(userId: String) {
        val used = JSONArray(tokenStore.getUsedTokensJson())

        for (i in 0 until used.length()) {
            val token = used.getJSONObject(i)
            saveTransaction(token)
        }

        // Clear used queue after sync
        tokenStore.setUsedTokensJson("[]")
    }

    /**
     * Save transaction (backend later)
     */
    private fun saveTransaction(token: JSONObject) {
        // TODO:
        // POST /nfc/pay
        // validate token
        // deduct balance
        // store transaction
    }

    fun availableTokenCount(): Int {
        return JSONArray(tokenStore.getAvailableTokensJson()).length()
    }
}
