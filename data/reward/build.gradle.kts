
plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.ktlint.gradle)
    alias(libs.plugins.ksp)
}

apply(from = rootProject.file("gradle/ktlint.gradle"))

dependencies {

    implementation(libs.kotlin.stdlib)

    //Dagger
    implementation(libs.hilt.android)

    //Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)

    //Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.adapter)

    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.coroutines.test)
    testImplementation(project(path = ":test:reward"))

    implementation(project(path = ":domain:reward"))
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.reward"
}
