
plugins {
    alias(libs.plugins.rewardedtodo.android.library)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.junit)
}

android {
    namespace = "jp.kztproject.rewardedtodo.common.kvs"
}

