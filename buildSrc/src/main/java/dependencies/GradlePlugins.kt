package dependencies

object GradlePlugins {
    object Android {
        const val version = "4.2.1"
    }
    object Kotlin {
        const val version = "1.4.20"
    }
    object Hilt {
        const val version = "2.35"
    }
    const val android = "com.android.tools.build:gradle:${Android.version}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
    const val navigation_safe_args = "androidx.navigation:navigation-safe-args-gradle-plugin:${Libraries.AndroidX.NavigationComponent.version}"
    const val dagger_hilt = "com.google.dagger:hilt-android-gradle-plugin:${Hilt.version}"
}
