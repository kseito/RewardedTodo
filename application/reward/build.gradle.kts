import dependencies.GradlePlugins
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint.gradle)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.hilt.android)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.adapter)

    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(project(":test:reward"))

    implementation(project(":domain:reward"))
    implementation(project(":data:ticket"))
}

android {
    namespace = "jp.kztproject.rewardedtodo.application.reward"
}

configure<KtlintExtension> {
    version.set(GradlePlugins.Ktlint.version)
    android.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
    }
    ignoreFailures.set(true)
}
