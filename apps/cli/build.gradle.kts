plugins {
    kotlin("jvm")
    kotlin("plugin.noarg")
}

dependencies {
    implementation(project(":libs:logging"))
    implementation(project(":libs:core"))

    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.slf4j:slf4j-reload4j:2.0.17")
    implementation("commons-io:commons-io:2.21.0")
    implementation("info.picocli:picocli:4.7.7")

    testImplementation(kotlin("test"))
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