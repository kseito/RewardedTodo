
plugins {
    alias(libs.plugins.rewardedtodo.android.library)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    api(libs.datastore.preferences)
    testImplementation(libs.junit)
}

android {
    namespace = "jp.kztproject.rewardedtodo.common.kvs"
}

