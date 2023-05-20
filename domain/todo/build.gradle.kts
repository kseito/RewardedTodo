import dependencies.Libraries

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
}

apply(from = rootProject.file("gradle/android_common.gradle"))
apply(from = rootProject.file("gradle/ktlint.gradle"))

android {
    namespace = "jp.kztproject.rewardedtodo.domain.todo"

}

dependencies {
    implementation(Libraries.Kotlin.stdlib)
    implementation(Libraries.Kotlin.Coroutines.core)

    testImplementation(Libraries.Test.junit)
}
