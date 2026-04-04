plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")  // ADD THIS
}

android {
    namespace = "com.example.appdevadmin"
    compileSdk = 36  // bumped to 36 to satisfy dependencies

    defaultConfig {
        applicationId = "com.example.appdevadmin"
        minSdk = 24
        targetSdk = 35  // this can stay at 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))  // ADD THIS
    implementation("com.google.firebase:firebase-auth")                   // ADD THIS
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}