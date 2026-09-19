plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    // Lists the app's libraries and their licences in R.raw.aboutlibraries.
    alias(libs.plugins.aboutlibraries.android)
}

android {
    namespace = "com.example.template"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.template"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = 2
        versionName = "0.2.0"
    }

    // Optional release signing, from a Gradle property or else an environment variable of the same meaning.
    // Without a keystore the release APK is built unsigned.
    fun secret(property: String, variable: String): String? =
        providers.gradleProperty(property).orElse(providers.environmentVariable(variable)).orNull
    val keystore = secret("release.storeFile", "RELEASE_STORE_FILE")
    signingConfigs {
        if (keystore != null) {
            create("release") {
                storeFile = file(keystore)
                storePassword = secret("release.storePassword", "RELEASE_STORE_PASSWORD")
                keyAlias = secret("release.keyAlias", "RELEASE_KEY_ALIAS")
                keyPassword = secret("release.keyPassword", "RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            // Installs next to a release build instead of failing on its other key.
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }

    lint {
        warningsAsErrors = true
    }

    dependenciesInfo {
        // The dependency block Google Play reads; F-Droid rejects APKs that carry it.
        includeInApk = false
        includeInBundle = false
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
}
