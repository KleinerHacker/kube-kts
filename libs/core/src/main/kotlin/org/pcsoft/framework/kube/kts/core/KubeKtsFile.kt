package org.pcsoft.framework.kube.kts.core

interface KubeKtsFile : KubeFile {
    val script: String
}

internal data class DefaultKubeKtsFile(
    override val subject: String,
    override val type: KubeFile.Type,
    override val script: String
) : KubeKtsFile