plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "kube-kts"
include("libs:api")
include("libs:definition")
include("libs:core")