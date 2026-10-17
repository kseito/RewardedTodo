plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)

    // Dagger
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit.core)

    // Coroutines
    implementation(libs.coroutines.core)

    implementation(project(":domain:todo"))
    implementation(project(":common:kvs"))
    implementation(project(":data:todoist"))
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.todo"
}