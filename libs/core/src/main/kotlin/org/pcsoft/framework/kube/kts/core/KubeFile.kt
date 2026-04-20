package org.pcsoft.framework.kube.kts.core

interface KubeFile {
    val subject: String
    val type: Type

    enum class Type(val subject: String) {
        CHART("chart"),
        TEMPLATE("");

        companion object {
            fun fromPath(subject: String): Type =
                entries.firstOrNull { it.subject.equals(subject, true) } ?: TEMPLATE
        }
    }
}