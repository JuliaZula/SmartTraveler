plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.smarttraveler_v1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smarttraveler_v1"
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
        sourceCompatibility = JavaVersion.VERSION_14
        targetCompatibility = JavaVersion.VERSION_14
    }
    packaging {
        resources {
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(libs.retrofit)
    implementation(libs.converterGson)


    implementation (libs.jsprit)

    implementation (libs.graphhopper.core)

    implementation (libs.guava)
    
    implementation (libs.commons.math3)

    implementation ("androidx.room:room-runtime:2.6.1")

    implementation("com.opencsv:opencsv:5.10")

    implementation ("com.google.android.material:material:1.9.0")

    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.24")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")



    testImplementation(libs.junit)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}