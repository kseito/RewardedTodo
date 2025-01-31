import dependencies.GradlePlugins
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ktlint.gradle)
}

android {
    compileSdk = Versions.androidCompileSdkVersion

    defaultConfig {
        applicationId = Packages.name
        minSdk = Versions.androidMinSdkVersion
        targetSdk = Versions.androidTargetSdkVersion
        versionCode = Versions.versionCode
        versionName = Versions.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TODOIST_URL", "\"https://todoist.com\"")
        buildConfigField("String", "REWARD_LIST_SERVER_URL", "\"https://rewardlist.herokuapp.com\"")
    }

    signingConfigs {
        create("staging") {
            storeFile = rootProject.file("staging.keystore")
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
        create("staging") {
            signingConfig = signingConfigs.getByName("staging")
            applicationIdSuffix = ".beta"
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            matchingFallbacks += listOf("debug")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString() // or "17"
    }
    packagingOptions {
        resources {
            excludes += listOf(
                "META-INF/LICENSE.txt",
                "META-INF/todo_debug.kotlin_module",
                "META-INF/reward_debug.kotlin_module",
                "META-INF/auth_debug.kotlin_module",
                "META-INF/metadata.kotlin_module",
                "META-INF/metadata.jvm.kotlin_module",
                "META-INF/kotlinx-metadata-jvm.kotlin_module",
                "META-INF/elements.kotlin_module",
                "META-INF/kotlinx-metadata.kotlin_module",
                "META-INF/core.kotlin_module",
                "META-INF/specs.kotlin_module",
                "META-INF/gradle/incremental.annotation.processors"
            )
        }
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
    sourceSets {
        getByName("test") {
            java.srcDirs("src/test/kotlin")
        }
    }
    namespace = "jp.kztproject.rewardedtodo"
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.activity.compose)
    implementation(libs.material)
    implementation(libs.preference)
    implementation(libs.datastore.preferences)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.security.crypto)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.coroutines.adapter)
    implementation(libs.gson)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.interceptor)
    implementation(libs.moshi)

    implementation(project(":feature:reward"))
    implementation(project(":feature:auth"))
    implementation(project(":common:kvs"))

    // TODO I don`t know why this module is needed
    implementation(project(":feature:todo"))
    implementation(project(":feature:setting"))
    implementation(project(":data:todo"))
    implementation(project(":data:ticket"))
    implementation(project(":common:database"))
    implementation(project(":domain:reward"))
    implementation(project(":data:reward"))
    implementation(project(":data:auth"))
    implementation(project(":data:todoist"))
    implementation(project(":application:reward"))
    implementation(project(":application:todo"))
    implementation(project(":domain:todo"))
}

kapt {
    correctErrorTypes = true
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set(GradlePlugins.Ktlint.version)
    android.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    ignoreFailures.set(true)
}
