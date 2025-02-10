plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint.gradle)
}

apply(from = rootProject.file("gradle/ktlint.gradle"))

dependencies {
    implementation(libs.kotlin.stdlib)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.coroutines.adapter)
    implementation(libs.retrofit.moshi)
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.todoist"
}
