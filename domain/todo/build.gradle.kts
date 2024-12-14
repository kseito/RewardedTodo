
plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint.gradle)
}

apply(from = rootProject.file("gradle/ktlint.gradle"))

android {
    namespace = "jp.kztproject.rewardedtodo.domain.todo"

}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.coroutines.core)

    testImplementation(libs.junit)
}
