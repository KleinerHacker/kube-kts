package org.pcsoft.framework.kube.kts.core

import java.nio.file.Path

interface KubeKtsFile : KubeFile {
    val script: String
}

internal data class DefaultKubeKtsFile(
    override val subject: String,
    override val relativePath: Path,
    override val script: String
) : KubeKtsFile