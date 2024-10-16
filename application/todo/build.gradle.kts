import dependencies.Libraries

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

    testImplementation(Libraries.Test.junit)

    //Dagger
    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.compiler)

    //Coroutines
    implementation(libs.coroutines.core)
}
