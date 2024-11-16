
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
    kotlin("kapt")
}

apply(from = rootProject.file("gradle/android_common.gradle"))
apply(from = rootProject.file("gradle/ktlint.gradle"))

android {
    namespace = "jp.kztproject.rewardedtodo.application.todo"
}

dependencies {

    //TODO need to reverse dependencies when todo module separate application layer and domain layer
    implementation(project(path = ":data:ticket"))
    implementation(project(path = ":domain:todo"))

    implementation(libs.kotlin.stdlib)

    implementation(libs.core.ktx)

    testImplementation(libs.junit)

    //Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //Coroutines
    implementation(libs.coroutines.core)
}
