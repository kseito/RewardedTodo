plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

apply(from = rootProject.file("gradle/android_common.gradle"))

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)

    testImplementation(libs.junit)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}

android {
    namespace = "jp.kztproject.rewardedtodo.common.database"
}