
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ktlint.gradle)
    alias(libs.plugins.kotlin.kapt)
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
    implementation(libs.fragment.ktx)
    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)

    //Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    testImplementation(libs.junit)
}

apply(from = rootProject.file("gradle/ktlint.gradle"))
