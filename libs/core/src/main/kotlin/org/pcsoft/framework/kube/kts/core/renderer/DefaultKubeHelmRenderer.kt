package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmFile

internal object DefaultKubeHelmRenderer : KubeHelmRendererBase() {
    override fun render(file: KubeHelmFile): String {
        return mapper.writeValueAsString(file.spec)
    }
}