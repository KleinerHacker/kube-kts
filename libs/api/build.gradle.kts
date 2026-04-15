plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("tools.jackson.dataformat:jackson-dataformat-yaml:3.1.2")
    implementation("tools.jackson.module:jackson-module-kotlin:3.1.2")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}