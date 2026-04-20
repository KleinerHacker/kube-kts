plugins {
    kotlin("jvm")
    kotlin("plugin.noarg")
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