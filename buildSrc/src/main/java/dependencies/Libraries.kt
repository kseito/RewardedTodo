package dependencies

@Suppress("MayBeConstant")
object Libraries {

    object Google {
        const val truth = "com.google.truth:truth:1.4.4"
    }

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val kotest = "io.kotest:kotest-runner-junit5:5.9.1"
        const val androidXCore = "androidx.test:core:1.6.1"
        const val mockito = "org.mockito:mockito-core:5.14.2"
        const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:5.4.0"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:4.12.0"
        const val robolectric = "org.robolectric:robolectric:4.13"
        const val assertJ = "org.assertj:assertj-core:3.26.3"
        const val mockk = "io.mockk:mockk:1.13.13"

        object AndroidX {
            const val junit = "androidx.test.ext:junit:1.2.1"
            const val coreTesting = "androidx.arch.core:core-testing:2.2.0"
            const val testRunner = "androidx.test:runner:1.6.2"
            const val espresso = "androidx.test.espresso:espresso-core:3.6.1"
        }
    }
}
