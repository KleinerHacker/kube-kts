/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

plugins {
    kotlin("jvm") version "2.3.20" apply false
    kotlin("plugin.noarg") version "2.3.20" apply false
    id("org.jetbrains.dokka") version "2.2.0"
}

group = "org.pcsoft.tooling"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

dokka {
    dokkaSourceSets {
        allprojects {
            if (name == "libs" || name == "apps")
                return@allprojects

            register(name) {
                sourceRoots.from(
                    "$projectDir/src/main/kotlin",
                )
            }
        }
    }
}

tasks {
    register<Copy>("copyDokka") {
        group = "dokka"
        description = "Copy all Dokka to MkDocs"

        from(File("build/dokka"))
        into(File("docs/docs/dokka"))

        dependsOn("dokkaGeneratePublicationHtml")
    }

    register<Delete>("deleteDokka") {
        group = "dokka"
        description = "Delete Dokka"
        delete(File("docs/docs/dokka"))
    }

    register<Exec>("installMkDocs") {
        group = null
        description = "Install mkdocs"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "mkdocs")
    }

    register<Exec>("installMkDocsMaterial") {
        group = null
        description = "Install mkdocs-material"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "mkdocs-material")
    }

    register<Exec>("installGitHubPages") {
        group = null
        description = "Install mkdocs-material"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "ghp-import")
    }

    register("installDocs") {
        group = "MKDocs"
        description = "Install mkdocs"

        dependsOn("installMkDocs")
        dependsOn("installMkDocsMaterial")
        dependsOn("installGitHubPages")
    }

    register<Exec>("runDocs") {
        group = "MKDocs"
        description = "Run mkdocs serve and open browser"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "serve", "-o", "-w", ".", "-w", "./docs/kts")

        dependsOn("installDocs")
        dependsOn("copyDokka")

        finalizedBy("deleteDokka")
    }

    register<Exec>("deployDocs") {
        group = "MKDocs"
        description = "Deploy mkdocs to gh-pages"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "gh-deploy", "--force")

        dependsOn("installDocs")
        dependsOn("copyDokka")

        finalizedBy("deleteDokka")
    }
}