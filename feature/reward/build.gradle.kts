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
    implementation(libs.navigation.fragment.ktx)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.compose.material.icon.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.livedata)
    implementation(libs.constraintlayout.compose)
    implementation(libs.hilt.navigation.compose)

    //Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

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
