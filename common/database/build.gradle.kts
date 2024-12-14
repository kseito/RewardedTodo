plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)

    testImplementation(libs.junit)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}

android {
    namespace = "jp.kztproject.rewardedtodo.common.database"
}