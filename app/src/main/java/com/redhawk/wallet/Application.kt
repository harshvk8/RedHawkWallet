package com.redhawk.wallet

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class RedHawkApp : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            FirebaseApp.initializeApp(this)
            Log.d("APP_START", "Firebase initialized")
        } catch (e: Exception) {
            Log.e("APP_START", "Firebase init failed: ${e.message}", e)
        }

        try {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d("APP_START", "Firebase App Check initialized")
        } catch (e: Exception) {
            Log.e("APP_START", "App Check init failed: ${e.message}", e)
        }
    }
}