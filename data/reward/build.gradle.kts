
plugins {
    alias(libs.plugins.rewardedtodo.android.library)
}

dependencies {
    implementation(project(":common:rewardserver"))

    implementation(libs.kotlin.stdlib)

    //Dagger
    implementation(libs.hilt.android)

    implementation(project(":common:kvs"))
    implementation(libs.datastore.preferences)
    implementation(libs.moshi)
    implementation(libs.retrofit.moshi)

    //Retrofit
    implementation(libs.retrofit.core)

    //Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.adapter)

    testImplementation(libs.junit)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(project(path = ":test:reward"))

    implementation(project(path = ":domain:reward"))
}

android {
    namespace = "jp.kztproject.rewardedtodo.data.reward"
}
