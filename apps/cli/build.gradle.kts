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
    implementation(project(":libs:api"))
    implementation(project(":libs:logging"))
    implementation(project(":libs:core"))

    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.slf4j:slf4j-reload4j:2.0.17")
    implementation("commons-io:commons-io:2.21.0")
    implementation("info.picocli:picocli:4.7.7")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.4")
}

kotlin {
    jvmToolchain(25)
}

noArg {
    annotation("org.pcsoft.framework.kube.kts.cli.intern.NoArgs")
    invokeInitializers = true
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("kube-kts")
    archiveVersion.set(rootProject.version.toString())
}