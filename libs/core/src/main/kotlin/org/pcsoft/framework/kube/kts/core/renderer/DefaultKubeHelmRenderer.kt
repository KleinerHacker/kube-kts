package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmFile
import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.successStyle
import org.pcsoft.framework.kube.kts.logging.symbolSubProcess

internal object DefaultKubeHelmRenderer : KubeHelmRendererBase() {
    private val logger = logger()

    override fun render(file: KubeHelmFile): String {
        logger.atDebug().log { "$symbolSubProcess Rendering to YAML: ${file.subject}" }

        return mapper.writeValueAsString(file.spec).apply {
            logger.atTrace().log { "Rendered: ${file.subject}".successStyle() }
        }
    }
}