plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.kapt)
    id("jacoco")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.robolectric)
    testImplementation(libs.truth)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.coroutines.test)

    // Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Retrofit
    implementation(libs.retrofit.core)

    // Coroutines
    implementation(libs.coroutines.core)

    implementation(project(":domain:todo"))
    implementation(project(":common:database"))
    implementation(project(":common:kvs"))
    implementation(project(":data:todoist"))
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.todo"
}