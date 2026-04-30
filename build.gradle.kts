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
    register<Exec>("installDocs") {
        group = "MKDocs"
        description = "Install mkdocs"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "mkdocs")
        commandLine("python", "-m", "pip", "install", "mkdocs-material")
        commandLine("python", "-m", "pip", "install", "mkdocs", "ghp-import")
        //commandLine("python", "-m", "pip", "install", "mkdocs", "pymdown-extensions")
    }

    register<Exec>("runDocs") {
        group = "MKDocs"
        description = "Run mkdocs serve and open browser"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "serve", "-o", "-w", ".", "-w", "./docs/kts")

        dependsOn("installDocs")
    }

    register<Exec>("deployDocs") {
        group = "MKDocs"
        description = "Deploy mkdocs to gh-pages"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "gh-deploy")

        dependsOn("installDocs")
    }
}