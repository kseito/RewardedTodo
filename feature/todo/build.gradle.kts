
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("jacoco")
}

apply(from = rootProject.file("gradle/android_common.gradle"))

android {
    buildFeatures {
        dataBinding = true
    }
    namespace = "jp.kztproject.rewardedtodo.feature.todo"

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    //TODO need to reverse dependencies when todo module separate application layer and domain layer
    implementation(project(path = ":application:todo"))
    implementation(project(path = ":domain:todo"))
    implementation(project(path = ":common:ui"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.material)

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

    testImplementation(libs.junit)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.coroutines.test)

    //Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}
