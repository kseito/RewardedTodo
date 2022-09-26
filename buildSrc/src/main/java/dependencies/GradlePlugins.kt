package dependencies

object GradlePlugins {
    object Android {
        const val version = "7.3.0"
    }
    object Kotlin {
        const val version = "1.7.0"
    }
    object Hilt {
        const val version = "2.42"
    }
    object KtlintGradle {
        const val version = "10.1.0"
    }
    const val android = "com.android.tools.build:gradle:${Android.version}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
    const val navigation_safe_args = "androidx.navigation:navigation-safe-args-gradle-plugin:${Libraries.AndroidX.NavigationComponent.version}"
    const val dagger_hilt = "com.google.dagger:hilt-android-gradle-plugin:${Hilt.version}"
    const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:${KtlintGradle.version}"
}
