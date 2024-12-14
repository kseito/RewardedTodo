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
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "rewardedtodo.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}