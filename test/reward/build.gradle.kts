plugins {
    alias(libs.plugins.rewardedtodo.android.library)
}

android {
    namespace = "jp.kztproject.rewardedtodo.test.reward"
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(project(":domain:reward"))
    implementation(project(":data:reward"))
}
