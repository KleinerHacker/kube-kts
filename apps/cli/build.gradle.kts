plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":libs:core"))

    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("commons-io:commons-io:2.21.0")
    implementation("info.picocli:picocli:4.7.7")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("kube-kts")
    archiveVersion.set(rootProject.version.toString())
}