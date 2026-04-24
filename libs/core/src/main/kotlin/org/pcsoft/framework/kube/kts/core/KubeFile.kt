package org.pcsoft.framework.kube.kts.core

import java.nio.file.Path

interface KubeFile {
    companion object {
        private const val CHART_SUBJECT = "chart"
    }

    val subject: String
    val relativePath: Path
    val isChart: Boolean get() = subject.equals(CHART_SUBJECT, true)
}

data class LegacyHelmFile(
    override val subject: String,
    override val relativePath: Path,
    val extension: String,
    val content: String
) : KubeFile