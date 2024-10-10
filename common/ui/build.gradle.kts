import dependencies.Libraries

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
    kotlin("kapt")
    alias(libs.plugins.ksp)
}

apply(from = rootProject.file("gradle/android_common.gradle"))

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(Libraries.AndroidX.coreKtx)

    // Jetpack Compose
    implementation(Libraries.AndroidX.Compose.foundation)
    implementation(Libraries.AndroidX.Compose.material)
    implementation(Libraries.AndroidX.Compose.uiTooling)

    testImplementation(Libraries.Test.junit)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}

android {
    namespace = "jp.kztproject.rewardedtodo.common.ui"
}
