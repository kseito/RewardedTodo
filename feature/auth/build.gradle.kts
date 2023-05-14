import dependencies.Libraries

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("jacoco")
    kotlin("kapt")
}

apply(from = rootProject.file("gradle/android_common.gradle"))

android {
    namespace = "jp.kztproject.rewardedtodo.feature.auth"
}

dependencies {
    implementation(project(path = ":data:auth"))

    implementation(Libraries.Kotlin.stdlib)
    implementation(Libraries.AndroidX.coreKtx)
    implementation(Libraries.AndroidX.appCompat)
    implementation(Libraries.AndroidX.fragment)
    implementation(Libraries.AndroidX.constraintLayout)
    implementation(Libraries.AndroidX.design)
    implementation(Libraries.AndroidX.LifeCycle.viewModelKtx)
    implementation(Libraries.AndroidX.NavigationComponent.fragmentKtx)

    //Dagger
    implementation(Libraries.Dagger.core)
    annotationProcessor(Libraries.Dagger.compiler)
    kapt(Libraries.Dagger.compiler)
    implementation(Libraries.Dagger.hilt)
    kapt(Libraries.Dagger.hiltCompiler)
}
