package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmFile
import org.pcsoft.framework.kube.kts.core.intern.utils.logger

internal object DefaultKubeHelmRenderer : KubeHelmRendererBase() {
    private val logger = logger()

    override fun render(file: KubeHelmFile): String {
        logger.atDebug().log { "Rendering KubeHelmFile: ${file.subject}" }

        return mapper.writeValueAsString(file.spec)
    }
}