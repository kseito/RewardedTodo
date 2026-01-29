plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    implementation(project(":domain:reward"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.material)
    implementation(libs.datastore.preferences)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.test)

    // Dagger
    implementation(libs.hilt.android)
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.ticket"
}
