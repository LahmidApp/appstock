plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") // Make sure this version is compatible with your Kotlin version
}

import java.util.Properties
import java.io.FileInputStream

android {
    namespace = "com.example.appstock"
    compileSdk = 36

    // Configuration de signature
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("app/keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                
                storeFile = file(keystoreProperties["STORE_FILE"].toString())
                storePassword = keystoreProperties["STORE_PASSWORD"].toString()
                keyAlias = keystoreProperties["KEY_ALIAS"].toString()
                keyPassword = keystoreProperties["KEY_PASSWORD"].toString()
            }
        }
    }

    defaultConfig {
        applicationId = "com.example.appstock"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Ajout d'informations personnalisées
        manifestPlaceholders["author"] = "LAHMID"
        manifestPlaceholders["developer"] = "LAHMID"
        resValue("string", "developer_signature", "LAHMID")
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            resValue("string", "app_author", "LAHMID")
            buildConfigField("String", "DEVELOPER_NAME", "\"LAHMID\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true // Supprime les ressources inutilisées (après la minification)
            resValue("string", "app_author", "LAHMID")
            buildConfigField("String", "DEVELOPER_NAME", "\"LAHMID\"")
            signingConfig = signingConfigs.getByName("release")

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
        }
    }
}

dependencies {
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.runtime.livedata)
    ksp(libs.androidx.room.room.compiler)

    // Optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.navigation.compose)

    // Optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.material.v1140)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.fragment.ktx) // Or the latest stable version
    implementation(libs.androidx.compose.material.material.icons.core) // You likely already have this or similar
    implementation(libs.androidx.material.icons.extended)

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")
    // ZXing (QR code)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.2")
    // Native Android PDF generation (no external dependencies needed)
    // Previously used iTextPDF but replaced with android.graphics.pdf.PdfDocument for better compatibility
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2") // Use the latest version

    // Coil pour l'affichage des images
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation(libs.material.icons.extended)
}

