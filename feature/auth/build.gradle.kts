import dependencies.Libraries

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint")
    kotlin("kapt")
}

apply(from = rootProject.file("gradle/android_common.gradle"))

android {
    defaultConfig {
        buildConfigField("String", "TODOIST_CLIENT_ID", "\"${System.getenv("TODOIST_CLIENT_ID")}\"")
        buildConfigField("String", "TODOIST_CLIENT_SECRET", "\"${System.getenv("TODOIST_CLIENT_SECRET")}\"")
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
    namespace = "jp.kztproject.rewardedtodo.feature.auth"
}

dependencies {
    implementation(project(path = ":data:auth"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(Libraries.AndroidX.fragment)
    implementation(libs.material)
    implementation(Libraries.AndroidX.LifeCycle.viewModelKtx)
    implementation(Libraries.AndroidX.NavigationComponent.fragmentKtx)

    //Dagger
    implementation(Libraries.Dagger.core)
    annotationProcessor(Libraries.Dagger.compiler)
    kapt(Libraries.Dagger.compiler)
    implementation(Libraries.Dagger.hilt)
    kapt(Libraries.Dagger.hiltCompiler)

    testImplementation(Libraries.Test.junit)
}

apply(from = rootProject.file("gradle/ktlint.gradle"))
