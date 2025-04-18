pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.deploygate" -> useModule("com.deploygate:gradle:${requested.version}")
            }
        }
    }
}

include(":common:kvs")
include(":data:auth")
include(":application:reward")
include(":test:reward")
include(":data:reward")
include(":domain:reward")
include(":data:ticket")
include(":common:database")
include(":data:todo")
include(":feature:todo")
include(":app")
include(":data:todoist")
include(":common:ui")
include(":feature:setting")
include(":feature:auth")
include(":feature:reward")
include(":application:todo")
include(":domain:todo")