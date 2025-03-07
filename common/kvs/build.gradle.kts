
plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.junit)
}

android {
    namespace = "jp.kztproject.rewardedtodo.common.kvs"
}

