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