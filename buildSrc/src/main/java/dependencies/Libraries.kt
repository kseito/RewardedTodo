package dependencies

object Libraries {
    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${GradlePlugins.Kotlin.version}"

        object Coroutines {
            const val version = "1.3.9"
            val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${version}"
            val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${version}"
            val adapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
        }
    }

    object AndroidX {
        val appCompat = "androidx.appcompat:appcompat:1.1.0"
        val design = "com.google.android.material:material:1.0.0"
        val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
        val dataBinding = "androidx.databinding:databinding-compiler:${GradlePlugins.Android.version}"
        val coreKtx = "androidx.core:core-ktx:1.3.2"
        val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"

        object LifeCycle {
            const val version = "2.2.0"
            val extensions = "androidx.lifecycle:lifecycle-extensions:${version}"
            val livedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${version}"
            val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${version}"
        }

        object Room {
            const val version = "2.2.4"
            val runtime = "androidx.room:room-runtime:${version}"
            val ktx = "androidx.room:room-ktx:${version}"
            val compiler = "androidx.room:room-compiler:${version}"
            val kaptCompiler = "androidx.room:room-compiler:${version}"
        }

        object NavigationComponent {
            const val version = "2.1.0"
            val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:${version}"
            val uiKtx = "androidx.navigation:navigation-ui-ktx:${version}"
        }
    }

    object Google {
        val truth = "com.google.truth:truth:1.0"
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
    val fbutton = "info.hoang8f:fbutton:1.0.5"
    val bottomNavigation = "com.aurelhubert:ahbottomnavigation:2.1.0"

    object Stetho {
        val stetho = "com.facebook.stetho:stetho:1.4.2"
    }

    object Test {
        val junit = "junit:junit:4.13.1"
        val androidXCore = "androidx.test:core:1.2.0"
        val mockito = "org.mockito:mockito-core:2.23.0"
        val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0"
        val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Kotlin.Coroutines.version}"
        val mockWebServer = "com.squareup.okhttp3:mockwebserver:3.11.0"
        val robolectric = "org.robolectric:robolectric:4.3.1"
        val assertJ = "org.assertj:assertj-core:3.12.2"

        object AndroidX {
            val junit = "androidx.test.ext:junit:1.1.2"
            val coreTesting = "androidx.arch.core:core-testing:2.0.0"
            val testRunner = "androidx.test:runner:1.1.0"
            val espresso = "androidx.test.espresso:espresso-core:3.1.0"
        }
    }
}