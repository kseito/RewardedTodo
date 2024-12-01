plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.ktlint.gradle)
}

apply(from = rootProject.file("gradle/android_common.gradle"))
apply(from = rootProject.file("gradle/ktlint.gradle"))

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
