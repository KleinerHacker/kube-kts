/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

import com.github.jk1.license.render.ReportRenderer

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
    id("com.github.jk1.dependency-license-report") version "2.5"
    id("app.cash.licensee") version "1.14.1" apply false
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

licenseReport {
    outputDir = layout.buildDirectory.dir("licences").get().asFile.absolutePath

    configurations = arrayOf("runtimeClasspath")

    renderers = arrayOf<ReportRenderer>(
        com.github.jk1.license.render.JsonReportRenderer(),
        com.github.jk1.license.render.SimpleHtmlReportRenderer()
    )
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        apply(plugin = "app.cash.licensee")
        plugins.withId("app.cash.licensee") {
            extensions.configure<app.cash.licensee.LicenseeExtension> {
                listOf(
                    // permissive
                    "Apache-2.0",
                    "MIT",
                    "BSD-2-Clause",
                    "BSD-3-Clause",
                    "ISC",

                    // weitere permissive
                    "Unlicense",
                    "Zlib",
                    "0BSD",

                    // bewusst erlaubte weak copyleft
                    "MPL-2.0",
                    "LGPL-2.1",
                    "LGPL-3.0",

                    // ecosystem
                    "CDDL-1.0",
                    "CDDL-1.1",
                    "EPL-1.0",
                    "EPL-2.0",

                    "Unlicense",
                    "CC0-1.0",
                ).forEach(::allow)

                allowUrl("https://opensource.org/license/mit")
            }
        }
    }
}

tasks {
    //region Dokka
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
    //endregion

    //region Licencing
    register<Copy>("copyLicenceReport") {
        group = "licencing"
        description = "copy licence report to MK Docs"
        from(File("build/licences"))
        into(File("docs/docs/licences"))

        dependsOn("generateLicenseReport")
    }

    register<Delete>("deleteLicenceReport") {
        group = "licencing"
        description = "Delete licence report"
        delete(File("docs/docs/licences"))
    }
    //endregion

    //region MK Docs
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
        dependsOn("copyLicenceReport")

        finalizedBy("deleteDokka")
        finalizedBy("deleteLicenceReport")
    }

    register<Exec>("deployDocs") {
        group = "MKDocs"
        description = "Deploy mkdocs to gh-pages"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "gh-deploy", "--force")

        dependsOn("installDocs")
        dependsOn("copyDokka")
        dependsOn("copyLicenceReport")

        finalizedBy("deleteDokka")
        finalizedBy("deleteLicenceReport")
    }
    //endregion
}