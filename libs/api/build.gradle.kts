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
    kotlin("jvm")
    kotlin("plugin.noarg")
    id("org.jetbrains.dokka")
}

dependencies {
    implementation(kotlin("reflect"))

    implementation("tools.jackson.core:jackson-databind:3.1.2")
    implementation("commons-io:commons-io:2.21.0")

    testImplementation(kotlin("test"))
    testImplementation("org.skyscreamer:jsonassert:1.5.3")
    testImplementation("tools.jackson.dataformat:jackson-dataformat-yaml:3.1.2")
    testImplementation("tools.jackson.module:jackson-module-kotlin:3.1.2")
}

noArg {
    annotation("org.pcsoft.framework.kube.kts.api.intern.NoArgs")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("kube-kts-${project.projectDir.name.lowercase()}")
    archiveVersion.set(rootProject.version.toString())
}