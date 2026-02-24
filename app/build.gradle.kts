plugins {
    alias(libs.plugins.android.application)
//<<<<<<< feature-ui
    alias(libs.plugins.kotlin.android)
=======
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
//>>>>>>> dev
}

android {
    namespace = "com.redhawk.wallet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.redhawk.wallet"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
//<<<<<<< feature-ui

=======
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
//>>>>>>> dev
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation("androidx.navigation:navigation-compose:2.7.7")

    debugImplementation(libs.androidx.compose.ui.tooling)
}