plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "jp.kztproject.rewardedtodo.application.todo"
}

dependencies {

    implementation(project(path = ":domain:reward"))
    implementation(project(path = ":domain:todo"))

    implementation(libs.kotlin.stdlib)

    implementation(libs.core.ktx)

    testImplementation(libs.junit)

    //Dagger
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //Coroutines
    implementation(libs.coroutines.core)
}
