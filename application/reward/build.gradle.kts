plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.hilt.android)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.adapter)

    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(project(":test:reward"))

    implementation(project(":domain:reward"))
    implementation(project(":data:ticket"))
}

android {
    namespace = "jp.kztproject.rewardedtodo.application.reward"
}
