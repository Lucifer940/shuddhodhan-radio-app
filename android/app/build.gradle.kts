plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}

// --- Play Store signing: read keystore.properties if present ---
// Expected file: keystore.properties at project root or app/ folder
// Contains:
//   storeFile=upload-keystore.jks (or .p12)
//   storePassword=***
//   keyAlias=upload
//   keyPassword=***
import java.util.Properties
import java.io.FileInputStream

val keystorePropsFile = rootProject.file("keystore.properties")
val appKeystorePropsFile = project.file("keystore.properties")
val propsFile = when {
    keystorePropsFile.exists() -> keystorePropsFile
    appKeystorePropsFile.exists() -> appKeystorePropsFile
    else -> null
}
val keystoreProps = Properties()
if (propsFile != null) {
    FileInputStream(propsFile).use { keystoreProps.load(it) }
    println("✅ Loaded signing config from ${propsFile.absolutePath}")
} else {
    println("⚠️ keystore.properties not found — release will use debug keystore (for CI/testing). Create keystore.properties from keystore.properties.example for Play Store signing.")
}

android {
    namespace = "com.radioshuddhodhan.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.radioshuddhodhan.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 10001
        versionName = "1.0.1"

        vectorDrawables { useSupportLibrary = true }
    }

    signingConfigs {
        // Debug is auto-provided by Android Gradle Plugin
        create("release") {
            if (propsFile != null) {
                val storeFilePath = keystoreProps["storeFile"] as String
                // Resolve storeFile relative to props file location, or project root, or app folder
                val possibleFiles = listOf(
                    propsFile.parentFile.resolve(storeFilePath),
                    rootProject.file(storeFilePath),
                    project.file(storeFilePath),
                    rootProject.file("app/$storeFilePath"),
                    file(storeFilePath)
                )
                val resolvedStoreFile = possibleFiles.firstOrNull { it.exists() } ?: file(storeFilePath)
                storeFile = resolvedStoreFile
                storePassword = keystoreProps["storePassword"] as String
                keyAlias = keystoreProps["keyAlias"] as String
                keyPassword = keystoreProps["keyPassword"] as String
                println("🔑 Release signing: storeFile=${storeFile?.absolutePath}, alias=$keyAlias")
            } else {
                // Fallback — will be overridden to debug in buildTypes if no props
                println("⚠️ No keystore.properties — release signing config will fallback to debug")
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            // Release-ready configuration. R8 is intentionally disabled for the
            // initial v1.0.1 release so the first public build is maximally stable.
            // To enable shrinking set isMinifyEnabled/isShrinkResources to true;
            // proguard-rules.pro already contains the required keep rules.
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Use real release keystore if keystore.properties exists, otherwise debug (CI)
            signingConfig = if (propsFile != null && (keystoreProps["storeFile"] as? String)?.isNotBlank() == true) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    lint {
        // Keep release builds from being blocked by lintVital on first CI runs.
        // Revisit for a production hardening pass.
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    // Java 8+ API desugaring (java.time on API < 26)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")

    // Core Android
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-service:2.8.7")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Room (local database / offline cache)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Network (backend integration point)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Media3 / ExoPlayer (live radio streaming + background playback)
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.5.1")
    implementation("androidx.media3:media3-session:1.5.1")

    // Preferences & background sync
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // Push notifications integration point.
    // NOTE: firebase-messaging is included WITHOUT the google-services plugin.
    // The app guards all Firebase access at runtime; until a Firebase project
    // (google-services.json) is added, push delivery is handled by the backend/API
    // and local notifications. See docs/BACKEND_API.md.
    implementation("com.google.firebase:firebase-messaging:24.1.0")

    // Unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // Instrumented tests
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
