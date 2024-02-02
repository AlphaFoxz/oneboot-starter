dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    api("org.springframework.boot:spring-boot-starter-jooq")
    api("org.postgresql:postgresql")
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "postgres_starter"
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
