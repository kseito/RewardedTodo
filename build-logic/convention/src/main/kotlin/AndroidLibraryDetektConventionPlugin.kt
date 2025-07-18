import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

class AndroidLibraryDetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure<DetektExtension> {
                config.setFrom(files("$rootDir/detekt.yml"))
            }

//            tasks.register<Detekt>("detektAll") {
//                description = "Runs Detekt on the whole project at once"
//                setSource(projectDir)
//                config.setFrom(rootProject.files("detekt.yml"))
//                baseline.set(file("detekt-baseline.xml"))
//                include("**/*.kt", "**/*.kts")
//                exclude("**/build/", "**/buildSrc/", "**/resources/", "**/tmp/", "**/*.kts")
//                reports {
//                    html.required.set(true)
//                    xml.required.set(true)
//                    txt.required.set(false)
//                    sarif.required.set(false)
//                    md.required.set(false)
//                }
//            }
        }
    }
}