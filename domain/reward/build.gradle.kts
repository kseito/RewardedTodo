plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
}

android {
    namespace = "jp.kztproject.rewardedtodo.domain.reward"
}
