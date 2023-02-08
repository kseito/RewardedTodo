import dependencies.Libraries

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("jacoco")
}

apply(from = rootProject.file("gradle/android_common.gradle"))

dependencies {

    implementation(Libraries.AndroidX.coreKtx)
    implementation(Libraries.AndroidX.appCompat)
    implementation(Libraries.AndroidX.design)

    implementation(Libraries.Test.junit)
}

android {
    namespace = "jp.kztproject.rewardedtodo.feature.setting"
}
