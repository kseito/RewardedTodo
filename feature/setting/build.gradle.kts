
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

    implementation(libs.core.ktx)
    implementation(libs.material)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.livedata)
    implementation(libs.constraintlayout.compose)
    implementation(libs.hilt.navigation.compose)

    //Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    testImplementation(libs.junit)
}

android {
    namespace = "jp.kztproject.rewardedtodo.feature.setting"
}
