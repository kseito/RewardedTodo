plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.deploy.gate) apply false
    alias(libs.plugins.rewardedtodo.android.application.spotless)
    alias(libs.plugins.spotless)
    alias(libs.plugins.rewardedtodo.android.application.deploygate)
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.google.com") }
    }
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.google.com") }
    }
}
