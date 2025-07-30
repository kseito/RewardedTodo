
plugins {
    `kotlin-dsl`
}

group = "jp.kztproject.rewardedtodo.buildlogic"

kotlin {
    compilerOptions {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    compileOnly(libs.spotless.gradle.plugin)
    compileOnly(libs.deploygate.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "rewardedtodo.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidApplicationSpotless") {
            id = "rewardedtodo.android.application.spotless"
            implementationClass = "AndroidApplicationSpotlessConventionPlugin"
        }
        register("androidApplicationDeploygate") {
            id = "rewardedtodo.android.application.deploygate"
            implementationClass = "AndroidApplicationDeploygateConventionPlugin"
        }
        register("androidLibraryDetekt") {
            id = "rewardedtodo.android.library.detekt"
            implementationClass = "AndroidLibraryDetektConventionPlugin"
        }
    }
}