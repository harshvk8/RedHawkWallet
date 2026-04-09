package com.redhawk.wallet.feature_auth

import android.content.Context

class SessionManager(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_EMAIL_VERIFIED = "is_email_verified"
    }

    /**
     * Save login state
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            .apply()
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Save email verification status
     */
    fun setEmailVerified(isVerified: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_EMAIL_VERIFIED, isVerified)
            .apply()
    }

    /**
     * Check if email is verified
     */
    fun isEmailVerified(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_EMAIL_VERIFIED, false)
    }

    /**
     * Clear ALL session data (🔥 critical for fixing your bug)
     */
    fun clearSession() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}