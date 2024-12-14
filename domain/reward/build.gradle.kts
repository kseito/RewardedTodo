import dependencies.GradlePlugins

plugins {
    alias(libs.plugins.rewardedtodo.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint.gradle)
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

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set(GradlePlugins.Ktlint.version)
    android.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    ignoreFailures.set(true)
}
