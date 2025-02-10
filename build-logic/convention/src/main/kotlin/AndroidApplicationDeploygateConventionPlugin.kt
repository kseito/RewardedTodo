import com.deploygate.gradle.plugins.dsl.DeployGateExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationDeploygateConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("deploygate")

            extensions.configure<DeployGateExtension> {
                appOwnerName = System.getenv("DEPLOY_GATE_OWENER_NAME")
                apiToken = System.getenv("DEPLOY_GATE_API_KEY")
            }
        }
    }
}
