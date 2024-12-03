import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    compileSdk = Versions.androidCompileSdkVersion

    defaultConfig {
        minSdk = Versions.androidMinSdkVersion
        targetSdk = Versions.androidTargetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString() // or "17"
    }
    namespace = "jp.kztproject.rewardedtodo.test.reward"
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(project(":domain:reward"))
    implementation(project(":data:reward"))
}