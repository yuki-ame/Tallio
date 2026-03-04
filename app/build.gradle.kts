plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.yuvraj.tallio_demo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yuvraj.tallio_demo"
        minSdk = 26
        targetSdk = 35
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
}

dependencies {
    // Room Database - for local storage
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // MPAndroidChart - for pie chart and bar graph
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Biometric - for fingerprint authentication
    implementation("androidx.biometric:biometric:1.1.0")

    // Standard Android libraries
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Pinned versions for compatibility
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.core:core:1.13.1")

    dependencies {
        // ... other dependencies (AppCompat, Material, etc.)

        // Standard Unit Test dependency
        testImplementation ("junit:junit:4.13.2")

        // Android Instrumentation Test dependencies (for UI tests)
        androidTestImplementation ("androidx.test.ext:junit:1.1.5")
        androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    }
}
