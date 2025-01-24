import dependencies.GradlePlugins
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.ktlint.gradle) apply false
    alias(libs.plugins.deploy.gate) apply false
    alias(libs.plugins.rewardedtodo.android.application.spotless)
    alias(libs.plugins.spotless)
//    id("org.gradle.jacoco")
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// FIXME: Not working. migrate to composite build.
//configure<JacocoPluginExtension> {
//    toolVersion = "0.8.12"
//}
//
//tasks.register("jacocoMergeReports", JacocoReport::class) {
//    group = "Reporting"
//    description = "Merge all JaCoCo reports from projects into one."
//
//    gradle.afterProject {
//        subprojects.forEach { project ->
//            if (project != rootProject && project.plugins.hasPlugin("jacoco")) {
//                dependsOn("${project.path}:testDebugUnitTest")
//                executionData(project.fileTree(mapOf("dir" to "${project.buildDir}/jacoco", "include" to listOf("testDebugUnitTest.exec"))))
//            }
//        }
//    }
//}
//
//tasks.register("jacocoTestReports", JacocoReport::class) {
//    group = "Reporting"
//    description = "Generate Jacoco coverage reports for the build. Only unit tests."
//
//    dependsOn("jacocoMergeReports")
//    executionData.setFrom(
//        files(tasks.named("jacocoMergeReports").get())
//    )
//
//    gradle.projectsEvaluated {
//        subprojects.forEach { project ->
//            if (project != rootProject && project.plugins.hasPlugin("jacoco")) {
//                classDirectories.from(files(project.fileTree(mapOf("dir" to "${project.buildDir}/tmp/kotlin-classes/debug"))))
//                sourceDirectories.from(files("${project.projectDir}/src/main/java"))
//            }
//        }
//    }
//
//    reports {
//        xml.required.set(true)
//        html.required.set(true)
//        csv.required.set(false)
//
//        xml.outputLocation.set(file("${buildDir}/reports/jacoco/report.xml"))
//        html.outputLocation.set(file("${buildDir}/reports/jacoco/html"))
//    }
//}