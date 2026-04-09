plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // Only include this if you're using Firebase
    id("com.google.gms.google-services") version "4.4.4" apply false
}