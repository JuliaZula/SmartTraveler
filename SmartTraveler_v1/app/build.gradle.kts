import org.jetbrains.dokka.DokkaDefaults.includeNonPublic
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.dokka") version "1.9.0"
}


android {
    namespace = "com.example.smarttraveler_v1"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.smarttraveler_v1"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProps = Properties()
        val localPropsFile = rootProject.file("local.properties")
        if (localPropsFile.exists()) {
            localProps.load(FileInputStream(localPropsFile))
        }

        val apiBaseUrl = localProps.getProperty("API_BASE_URL") ?: project.findProperty("API_BASE_URL")?.toString()

        if (apiBaseUrl == null) {
            throw GradleException("API_BASE_URL is missing. Define it in local.properties or gradle.properties.")
        }

        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
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

tasks.dokkaHtml.configure {
    dokkaSourceSets.configureEach {
        sourceRoots.from(file("src/main/java"))
        includes.from("README.md")
        includeNonPublic.set(false)
        reportUndocumented.set(true)
        skipEmptyPackages.set(true)
        skipDeprecated.set(false)
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

    implementation ("com.google.android.material:material:1.12.0")

    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.24")

    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.9.0")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")



    testImplementation(libs.junit)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}