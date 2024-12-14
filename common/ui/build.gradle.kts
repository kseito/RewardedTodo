
plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.core.ktx)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}

android {
    namespace = "jp.kztproject.rewardedtodo.common.ui"
}
