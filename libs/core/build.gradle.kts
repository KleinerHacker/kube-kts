plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":libs:api"))
    implementation(project(":libs:definition"))

    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    implementation("commons-io:commons-io:2.21.0")
    implementation("tools.jackson.dataformat:jackson-dataformat-yaml:3.1.2")
    implementation("tools.jackson.module:jackson-module-kotlin:3.1.2")

    testImplementation(kotlin("test"))
    testImplementation("org.skyscreamer:jsonassert:1.5.3")
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