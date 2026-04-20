package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmFile

interface KubeHelmRenderer {
    fun render(file: KubeHelmFile)
}