import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import com.diffplug.gradle.spotless.SpotlessExtension

class AndroidApplicationSpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.diffplug.spotless")
            // Configure the Spotless plugin
            extensions.configure<SpotlessExtension> {
                kotlin {
                    ratchetFrom("origin/master")

                    target("**/*.kt")
                    targetExclude("**/build/**/*.kt")
                    toggleOffOn()

                    ktlint()
                        .editorConfigOverride(
                            mapOf(
                                "android" to "true",
                                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                            ),
                        )
                }
            }
        }
    }
}