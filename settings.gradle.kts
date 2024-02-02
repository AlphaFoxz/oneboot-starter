pluginManagement {
    val springbootPluginVersion: String by settings
    val springDependencyManagementPluginVersion: String by settings
    plugins {
        id("org.springframework.boot") version springbootPluginVersion
        id("io.spring.dependency-management") version springDependencyManagementPluginVersion
    }
}
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
        }
    }
}

rootProject.name = "oneboot-starter"

include(
    ":cache_starter",
    ":flowable_starter",
    ":meilisearch_starter",
    ":postgres_starter",
)

