
plugins {
    alias(libs.plugins.rewardedtodo.android.library)
}

android {
    namespace = "jp.kztproject.rewardedtodo.domain.todo"

}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.coroutines.core)

    testImplementation(libs.junit)
}
