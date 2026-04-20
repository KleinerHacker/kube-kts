package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmRepository
import java.nio.file.Path

interface KubeHelmRepositoryRenderer {
    companion object {
        val DEFAULT: KubeHelmRepositoryRenderer = DefaultKubeHelmRepositoryRenderer()
    }

    fun render(repository: KubeHelmRepository, targetPath: Path)
}