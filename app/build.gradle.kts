plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("com.github.TutorialsAndroid:GButton:v1.0.19")
    implementation ("com.google.android.gms:play-services-auth:20.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.firebase:firebase-analytics:21.4.0")
    implementation ("androidx.camera:camera-camera2:1.1.0")
    implementation ("androidx.camera:camera-lifecycle:1.1.0")
    implementation ("androidx.exifinterface:exifinterface:1.3.6")
    implementation ("androidx.cardview:cardview:1.0.0")
    // OkHttp for making API requests
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.github.bumptech.glide:glide:4.13.2")
//    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.2")
// Gson for parsing JSON responses
    implementation ("com.google.code.gson:gson:2.8.8")

    implementation ("com.google.mlkit:face-detection:16.1.5")

    implementation ("androidx.camera:camera-core:1.1.0")
    implementation ("com.google.guava:guava:31.1-android")

    implementation ("androidx.camera:camera-view:1.0.0-alpha32")
    implementation ("androidx.camera:camera-extensions:1.0.0-alpha32")
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}