import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.rewardedtodo.android.application.spotless)
    alias(libs.plugins.spotless)
    alias(libs.plugins.rewardedtodo.android.library.detekt)
    alias(libs.plugins.detekt)
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val wiremock: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    detektPlugins(project(":detekt-rules"))
    wiremock(libs.wiremock.standalone)
}

tasks.register<Sync>("syncWireMockJar") {
    description = "E2E用のWireMock standalone jarを build/wiremock/ へ配置する"
    group = "verification"
    from(wiremock)
    into(layout.buildDirectory.dir("wiremock"))
    rename { "wiremock-standalone.jar" }
}

// 既定の detekt は :app のみを対象にしているため、プロジェクト独自ルール
// (:detekt-rules) だけは全モジュールのソースに対して走らせる専用タスクを用意する。
// 既定ルールは detekt-custom.yml で無効化済みなので既存コードのノイズは出ない。
tasks.register<Detekt>("detektCustomRules") {
    description = "プロジェクト独自のdetektルールのみを全モジュールに対して実行する"
    group = "verification"
    parallel = true
    buildUponDefaultConfig = false
    config.setFrom(files("$rootDir/detekt-custom.yml"))
    setSource(files(projectDir))
    include("**/*.kt")
    exclude("**/build/**", "**/build-logic/**", "**/resources/**", "**/.*/**")
    reports {
        html.required.set(false)
        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}
