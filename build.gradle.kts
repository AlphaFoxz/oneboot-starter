val onebootStarterGroupId = "com.github.AlphaFoxz.oneboot-starter"
val onebootStarterVersion = "0.0.1-alpha.0"
plugins {
    id("java-library")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("maven-publish")
}
tasks.bootJar {
    enabled = false
}
tasks.jar {
    enabled = false
}
allprojects {
    group = onebootStarterGroupId
    version = onebootStarterVersion
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(1, TimeUnit.SECONDS)
    }
    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
subprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "maven-publish")
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    tasks.bootJar {
        enabled = false
    }
    tasks.jar {
        enabled = true
        archiveClassifier = ""
    }
    dependencyManagement {
        imports {
            mavenBom(project.property("parentProject") as String)
        }
        dependencies {
            dependency(project.property("parentProject") as String)
        }
    }
    dependencies {
        compileOnly("com.google.code.findbugs:annotations")
        compileOnly("org.springframework.boot:spring-boot-starter")
        implementation("com.github.AlphaFoxz:oneboot-core") {
            isChanging = true
        }
    }
}
