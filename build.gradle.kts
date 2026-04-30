plugins {
    kotlin("jvm") version "2.3.20" apply false
    kotlin("plugin.noarg") version "2.3.20" apply false
}

group = "org.pcsoft.tooling"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

tasks {
    register<Exec>("install") {
        group = "MKDocs"
        description = "Install mkdocs"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "mkdocs")
        commandLine("python", "-m", "pip", "install", "mkdocs-material")
        //commandLine("python", "-m", "pip", "install", "mkdocs", "pymdown-extensions")
    }

    register<Exec>("run") {
        group = "MKDocs"
        description = "Run mkdocs serve and open browser"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "serve", "-o", "-w", ".", "-w", "./docs/kts")

        dependsOn("install")
    }
}