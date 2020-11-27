package dependencies

object Libraries {
    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72"
    }

    object AndroidX {
        val appCompat = "androidx.appcompat:appcompat:1.1.0"
        val design = "com.google.android.material:material:1.0.0"
        val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
        val dataBinding = "androidx.databinding:databinding-compiler:${GradlePlugins.Android.version}"

        object Room {
            const val version = "2.2.4"
            val runtime = "androidx.room:room-runtime:${version}"
            val compiler = "androidx.room:room-compiler:${version}"
            val kaptCompiler = "androidx.room:room-compiler:${version}"
        }

        object NavigationComponent {
            const val version = "2.1.0"
            val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:${version}"
            val uiKtx = "androidx.navigation:navigation-ui-ktx:${version}"
        }
    }

    object Dagger {
        const val version = "2.20"
        val core = "com.google.dagger:dagger:${version}"
        val compiler = "com.google.dagger:dagger-compiler:${version}"
        val android = "com.google.dagger:dagger-android:${version}"
        val androidSupport =  "com.google.dagger:dagger-android-support:${version}"
        val androidProcesser = "com.google.dagger:dagger-android-processor:${version}"
    }

    object Retrofit {
        const val version = "2.4.0"
        val core = "com.squareup.retrofit2:retrofit:${version}"
        val gsonConverter = "com.squareup.retrofit2:converter-gson:${version}"
        val coroutinesAdapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
    }

    val gson = "com.google.code.gson:gson:2.8.2"

    object Stetho {
        val stetho = "com.facebook.stetho:stetho:1.4.2"
    }
}