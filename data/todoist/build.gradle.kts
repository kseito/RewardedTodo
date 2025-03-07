plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    implementation(libs.kotlin.stdlib)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.coroutines.adapter)
    implementation(libs.retrofit.moshi)
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.todoist"
}
