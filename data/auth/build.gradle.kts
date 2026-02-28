plugins {
    alias(libs.plugins.rewardedtodo.android.library)
}

dependencies {
    implementation(project(":common:kvs"))
    implementation(project(":data:todoist"))

    implementation(libs.kotlin.stdlib)

    // Coroutines
    implementation(libs.coroutines.core)

    // Dagger
    implementation(libs.hilt.android)

    testImplementation(libs.junit)
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.auth"
}
