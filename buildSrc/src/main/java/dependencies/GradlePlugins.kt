package dependencies

object GradlePlugins {
    object Android {
        const val version = "4.1.1"
    }
    object Kotlin {
        const val version = "1.3.72"
    }
    const val android = "com.android.tools.build:gradle:${Android.version}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
    const val navigation_safe_args = "androidx.navigation:navigation-safe-args-gradle-plugin:${Libraries.AndroidX.NavigationComponent.version}"
}