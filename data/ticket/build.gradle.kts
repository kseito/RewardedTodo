plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint.gradle)
}

apply(from = rootProject.file("gradle/android_common.gradle"))

dependencies {
    implementation(project(":domain:reward"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.material)
    implementation(libs.datastore.preferences)

    testImplementation(libs.junit)

    // Dagger
    implementation(libs.hilt.android)
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.ticket"
}

apply(from = rootProject.file("gradle/ktlint.gradle"))