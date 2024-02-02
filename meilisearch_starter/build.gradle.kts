dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("com.meilisearch.sdk:meilisearch-java:0.11.5")
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "meilisearch_starter"
            version = project.version.toString()
            from(components["java"])

            versionMapping {
                allVariants {
                    fromResolutionResult()
                }
            }
        }
    }
}
