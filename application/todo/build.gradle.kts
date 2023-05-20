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

    implementation(Libraries.Kotlin.stdlib)

    implementation(Libraries.AndroidX.appCompat)
    implementation(Libraries.AndroidX.coreKtx)

    testImplementation(Libraries.Test.junit)

    //Dagger
    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.compiler)

    //Coroutines
    implementation(Libraries.Kotlin.Coroutines.core)
}
