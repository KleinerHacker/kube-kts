package org.pcsoft.framework.kube.kts.core

interface KubeFile {
    val subject: String
    val type: Type

    enum class Type(val subject: String, val relativePath: String) {
        CHART("chart", "."),
        TEMPLATE("", "./templates");

        companion object {
            fun from(subject: String): Type =
                entries.firstOrNull { it.subject.equals(subject, true) } ?: TEMPLATE
        }
    }
}

data class LegacyHelmFile(
    override val subject: String,
    override val type: KubeFile.Type,
    val yaml: String
) : KubeFile