import dependencies.Libraries

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint")
    alias(libs.plugins.compose.compiler)
    kotlin("kapt")
}

apply(from = rootProject.file("gradle/android_common.gradle"))

android {
    namespace = "jp.kztproject.rewardedtodo.presentation.reward"
}

dependencies {

    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.material)
    implementation(Libraries.AndroidX.LifeCycle.viewModelKtx)
    implementation(Libraries.AndroidX.NavigationComponent.fragmentKtx)

    // Jetpack Compose
    implementation(libs.activity.compose)
    implementation(Libraries.AndroidX.Compose.foundation)
    implementation(Libraries.AndroidX.Compose.material)
    implementation(libs.compose.material.icon.core)
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

    //Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.adapter)

    //Test
    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.androidXCore)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(libs.coroutines.test)
    testImplementation(Libraries.Test.mockWebServer)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Test.assertJ)
    testImplementation(Libraries.Test.AndroidX.coreTesting)
    implementation(project(path = ":test:reward"))
    androidTestImplementation(Libraries.Test.AndroidX.junit)
    androidTestImplementation(Libraries.Test.AndroidX.espresso)

    implementation(project(path = ":application:reward"))
    implementation(project(path = ":domain:reward"))
    implementation(project(path = ":common:ui"))
}

apply(from = rootProject.file("gradle/ktlint.gradle"))
