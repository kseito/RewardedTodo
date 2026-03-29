plugins {
    alias(libs.plugins.rewardedtodo.android.library)
}

dependencies {
    implementation(project(":domain:reward"))
    implementation(project(":common:kvs"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.material)
    implementation(libs.datastore.preferences)

    // Network
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.interceptor)
    implementation(libs.moshi)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)

    // Dagger
    implementation(libs.hilt.android)
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.ticket"
}
