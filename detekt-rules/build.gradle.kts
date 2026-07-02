plugins {
    // バージョンはルートのビルドクラスパス（build-logic 経由）から解決するため指定しない
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.detekt.api)

    testImplementation(libs.detekt.test)
    testImplementation(libs.junit)
    testImplementation(libs.truth)
}

tasks.withType<Test> {
    useJUnit()
}
