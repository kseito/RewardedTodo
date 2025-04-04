plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "jp.kztproject.rewardedtodo.application.todo"
}

dependencies {

    //TODO need to reverse dependencies when todo module separate application layer and domain layer
    implementation(project(path = ":data:ticket"))
    implementation(project(path = ":domain:todo"))

    implementation(libs.kotlin.stdlib)

    implementation(libs.core.ktx)

    testImplementation(libs.junit)

    //Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //Coroutines
    implementation(libs.coroutines.core)
}
