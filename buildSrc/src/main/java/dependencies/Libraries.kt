package dependencies

@Suppress("MayBeConstant")
object Libraries {
    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${GradlePlugins.Kotlin.version}"

        object Coroutines {
            const val version = "1.6.4"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${version}"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${version}"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${version}"
            const val adapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
        }
    }

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:1.6.0"
        const val activityCompose = "androidx.activity:activity-compose:1.3.1"
        const val design = "com.google.android.material:material:1.8.0"
        const val fragment = "androidx.fragment:fragment-ktx:1.3.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
        const val coreKtx = "androidx.core:core-ktx:1.9.0"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
        const val preference = "androidx.preference:preference-ktx:1.1.1"
        const val dataStore = "androidx.datastore:datastore-preferences:1.0.0"

        object Compose {
            const val compilerVersion = "1.4.4"
            const val foundation = "androidx.compose.foundation:foundation:1.4.0"
            const val material = "androidx.compose.material:material:1.4.0"
            const val ui = "androidx.compose.ui:ui:1.4.0"
            const val uiTooling = "androidx.compose.ui:ui-tooling:1.4.0"
            const val liveData = "androidx.compose.runtime:runtime-livedata:1.4.0"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
            const val material3 = "androidx.compose.material3:material3:1.0.1"
            const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"
        }

        object LifeCycle {
            private const val version = "2.4.1"
            const val livedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${version}"
            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${version}"
        }

        object Room {
            private const val version = "2.4.2"
            const val runtime = "androidx.room:room-runtime:${version}"
            const val ktx = "androidx.room:room-ktx:${version}"
            const val compiler = "androidx.room:room-compiler:${version}"
            const val kaptCompiler = "androidx.room:room-compiler:${version}"
        }

        object NavigationComponent {
            const val version = "2.5.3"
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:${version}"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:${version}"
            const val navigationCompose = "androidx.navigation:navigation-compose:${version}"
        }

        object Security {
            const val crypto = "androidx.security:security-crypto:1.0.0"
        }
    }

    object Google {
        const val truth = "com.google.truth:truth:1.0"
    }

    object Dagger {
        private const val version = "2.44"
        const val core = "com.google.dagger:dagger:${version}"
        const val compiler = "com.google.dagger:dagger-compiler:${version}"
        const val hilt = "com.google.dagger:hilt-android:${GradlePlugins.Hilt.version}"
        const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${GradlePlugins.Hilt.version}"
    }

    object Retrofit {
        private const val version = "2.9.0"
        const val core = "com.squareup.retrofit2:retrofit:${version}"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:${version}"
        const val moshiConverter = "com.squareup.retrofit2:converter-moshi:${version}"
        const val coroutinesAdapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
    }

    object OkHttp {
        private const val version = "3.14.9"
        const val interceptor = "com.squareup.okhttp3:logging-interceptor:${version}"
    }

    object Moshi {
        private const val version = "1.12.0"
        const val moshi = "com.squareup.moshi:moshi-kotlin:${version}"
        const val adapters = "com.squareup.moshi:moshi-adapters:${version}"
        const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:${version}"
    }

    const val gson = "com.google.code.gson:gson:2.8.2"
    const val bottomNavigation = "com.aurelhubert:ahbottomnavigation:2.1.0"

    object Stetho {
        const val stetho = "com.facebook.stetho:stetho:1.4.2"
    }

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val androidXCore = "androidx.test:core:1.2.0"
        const val androidArchCore = "androidx.arch.core:core-testing:2.1.0"
        const val mockito = "org.mockito:mockito-core:3.11.1"
        const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:3.2.0"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Kotlin.Coroutines.version}"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:3.10.0"
        const val robolectric = "org.robolectric:robolectric:4.9"
        const val assertJ = "org.assertj:assertj-core:3.12.2"

        object AndroidX {
            const val junit = "androidx.test.ext:junit:1.1.3"
            const val coreTesting = "androidx.arch.core:core-testing:2.0.0"
            const val testRunner = "androidx.test:runner:1.4.0"
            const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
        }
    }
}
