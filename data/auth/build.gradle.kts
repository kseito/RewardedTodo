plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

dependencies {
    implementation(project(":common:kvs"))
    implementation(project(":data:todoist"))

    implementation(libs.kotlin.stdlib)

    // Coroutines
    implementation(libs.coroutines.core)

    // Dagger
    implementation(libs.hilt.android)

    testImplementation(libs.junit)
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.auth"
}
