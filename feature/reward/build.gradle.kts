
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ktlint.gradle)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.kapt)
}

apply(from = rootProject.file("gradle/android_common.gradle"))

android {
    namespace = "jp.kztproject.rewardedtodo.presentation.reward"
}

dependencies {

    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.material)
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
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.robolectric)
    testImplementation(libs.assertj.core)
    testImplementation(libs.androidx.arch.core.testing)
    implementation(project(path = ":test:reward"))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(project(path = ":application:reward"))
    implementation(project(path = ":domain:reward"))
    implementation(project(path = ":common:ui"))
}

apply(from = rootProject.file("gradle/ktlint.gradle"))
