package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmFile

interface KubeHelmRenderer {
    companion object {
        val DEFAULT: KubeHelmRenderer = DefaultKubeHelmRenderer
    }

    fun render(file: KubeHelmFile): String
}