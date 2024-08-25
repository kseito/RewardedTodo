import dependencies.Libraries

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("kapt")
}

apply(from = rootProject.file("gradle/android_common.gradle"))

dependencies {
    implementation(Libraries.Kotlin.stdlib)

    implementation(Libraries.AndroidX.appCompat)
    implementation(Libraries.AndroidX.coreKtx)

    // Jetpack Compose
    implementation(Libraries.AndroidX.Compose.foundation)
    implementation(Libraries.AndroidX.Compose.material)
    implementation(Libraries.AndroidX.Compose.uiTooling)

    testImplementation(Libraries.Test.junit)

    implementation(Libraries.AndroidX.Room.runtime)
}

android {
    namespace = "jp.kztproject.rewardedtodo.common.ui"
}
