plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.rewardedtodo.android.application.deploygate)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.rewardedtodo.android.library.detekt)
}

android {
    //noinspection GradleDependency
    compileSdk = libs.versions.androidCompileSdkVersion.get().toInt()

    defaultConfig {
        applicationId = "jp.kztproject.rewardedtodo"
        minSdk = libs.versions.androidMinSdkVersion.get().toInt()
        //noinspection OldTargetApi
        targetSdk = libs.versions.androidTargetSdkVersion.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
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
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            matchingFallbacks += listOf("debug")
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlin {
        jvmToolchain(17)
    }
    packaging {
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
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    lint {
        warningsAsErrors = true
    }
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
    implementation(libs.androidx.compose.material3)
    implementation(libs.compose.material.icon.core)
    implementation(libs.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.coroutines.adapter)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.interceptor)
    implementation(libs.moshi)

    // Showkase
    debugImplementation(libs.showkase)
    implementation(libs.showkase.annotation)
    kspDebug(libs.showkase.processor)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.ui.test.junit4)
    testImplementation(libs.espresso.core)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.rule)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(project(":feature:reward"))
    implementation(project(":feature:auth"))
    implementation(project(":common:kvs"))

    implementation(project(":feature:todo"))
    implementation(project(":feature:setting"))
    implementation(project(":data:todo"))
    implementation(project(":data:ticket"))
    implementation(project(":common:database"))
    implementation(project(":common:ui"))
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

// TODO: Move to convention plugin
roborazzi {
    outputDir.set(file("screenshots"))
}
