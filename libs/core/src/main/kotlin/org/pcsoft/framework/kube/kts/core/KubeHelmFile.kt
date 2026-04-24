package org.pcsoft.framework.kube.kts.core

import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import java.nio.file.Path

interface KubeHelmFile : KubeFile {
    val spec: KubeSpec
}

internal data class DefaultKubeHelmFile(
    override val subject: String,
    override val relativePath: Path,
    override val isChart: Boolean,
    override val spec: KubeSpec
) : KubeHelmFile {

    constructor(file: KubeKtsFile, spec: KubeSpec) : this(
        file.subject, file.relativePath, file.isChart, spec
    )

}