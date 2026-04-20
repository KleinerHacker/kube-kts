package org.pcsoft.framework.kube.kts.core.builder

import org.pcsoft.framework.kube.kts.core.KubeHelmRepository
import org.pcsoft.framework.kube.kts.core.KubeKtsRepository

interface KubeKtsRepositoryBuilder {
    companion object {
        val DEFAULT: KubeKtsRepositoryBuilder = DefaultKubeKtsRepositoryBuilder()
    }

    fun build(repository: KubeKtsRepository): KubeHelmRepository
}