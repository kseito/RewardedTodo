import dependencies.GradlePlugins
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint.gradle)
}

apply(from = rootProject.file("gradle/android_common.gradle"))

dependencies {
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.junit)
}

android {
    namespace = "jp.kztproject.rewardedtodo.common.kvs"
}

ktlint {
    version = GradlePlugins.Ktlint.version
    android = true
    reporters {
        reporter(ReporterType.CHECKSTYLE)
    }
    ignoreFailures = true
}

