import dependencies.Libraries

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("jacoco")
    alias(libs.plugins.compose.compiler)
    kotlin("kapt")
}

apply(from = rootProject.file("gradle/android_common.gradle"))

dependencies {
    implementation(project(path = ":data:auth"))

    implementation(Libraries.AndroidX.coreKtx)
    implementation(Libraries.AndroidX.design)
    implementation(libs.activity.compose)
    implementation(Libraries.AndroidX.Compose.foundation)
    implementation(Libraries.AndroidX.Compose.material)
    implementation(Libraries.AndroidX.Compose.material3)
    implementation(Libraries.AndroidX.Compose.ui)
    implementation(Libraries.AndroidX.Compose.uiTooling)
    implementation(Libraries.AndroidX.Compose.liveData)
    implementation(Libraries.AndroidX.Compose.constraintLayout)
    implementation(Libraries.AndroidX.Compose.hiltNavigationCompose)

    //Dagger
    implementation(Libraries.Dagger.core)
    annotationProcessor(Libraries.Dagger.compiler)
    kapt(Libraries.Dagger.compiler)
    implementation(Libraries.Dagger.hilt)
    kapt(Libraries.Dagger.hiltCompiler)

    testImplementation(Libraries.Test.junit)
}

android {
    namespace = "jp.kztproject.rewardedtodo.feature.setting"
}
