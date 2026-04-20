package org.pcsoft.framework.kube.kts.core

import org.pcsoft.framework.kube.kts.api.chart.KubeSpec

interface KubeHelmFile : KubeFile {
    val spec: KubeSpec
}

internal data class DefaultKubeHelmFile(
    override val subject: String,
    override val type: KubeFile.Type,
    override val spec: KubeSpec
) : KubeHelmFile {

    constructor(file: KubeKtsFile, spec: KubeSpec) : this(
        file.subject, file.type, spec
    )

}