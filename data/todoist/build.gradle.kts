plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(project(":domain:todo"))

    // Dependency Injection
    implementation(libs.hilt.android)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.coroutines.adapter)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.interceptor)
    implementation(libs.coroutines.core)
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.todoist"
}
