plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    buildFeatures {
        dataBinding = true
    }
    namespace = "jp.kztproject.rewardedtodo.feature.auth"
}

dependencies {
    implementation(project(path = ":data:auth"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)

    //Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    testImplementation(libs.junit)
}
