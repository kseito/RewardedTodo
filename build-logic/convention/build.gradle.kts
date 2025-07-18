import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "jp.kztproject.rewardedtodo.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
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
    }
    plugins {
        register("androidApplicationSpotless") {
            id = "rewardedtodo.android.application.spotless"
            implementationClass = "AndroidApplicationSpotlessConventionPlugin"
        }
    }
    plugins {
        register("androidApplicationDeploygate") {
            id = "rewardedtodo.android.application.deploygate"
            implementationClass = "AndroidApplicationDeploygateConventionPlugin"
        }
    }
    plugins {
        register("androidLibraryDetekt") {
            id = "rewardedtodo.android.library.detekt"
            implementationClass = "AndroidLibraryDetektConventionPlugin"
        }
    }
}