package jp.kztproject.rewardedtodo

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = libs.findVersion("androidCompileSdkVersion").get().requiredVersion.toInt()

        defaultConfig {
            minSdk = 31
        }
    }

    configure<KotlinAndroidProjectExtension> {
        compilerOptions.apply {
            jvmToolchain(17)
        }
    }
}
