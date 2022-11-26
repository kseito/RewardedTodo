import dependencies.Libraries

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

apply(from = rootProject.file("gradle/android_common.gradle"))

android {
    buildFeatures {
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Libraries.AndroidX.Compose.compilerVersion
    }
}

dependencies {

    // Jetpack Compose
    implementation(Libraries.AndroidX.activityCompose)
    implementation(Libraries.AndroidX.Compose.foundation)
    implementation(Libraries.AndroidX.Compose.material)
    implementation(Libraries.AndroidX.Compose.ui)
    implementation(Libraries.AndroidX.Compose.uiTooling)
    implementation(Libraries.AndroidX.Compose.liveData)
    implementation(Libraries.AndroidX.Compose.constraintLayout)
}
