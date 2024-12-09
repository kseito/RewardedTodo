plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint.gradle)
}

apply(from = rootProject.file("gradle/android_common.gradle"))
apply(from = rootProject.file("gradle/ktlint.gradle"))

dependencies {
    implementation(libs.kotlin.stdlib)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.coroutines.adapter)
    implementation(libs.retrofit.moshi)
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.todoist"
}
