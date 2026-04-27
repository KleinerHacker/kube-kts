package org.pcsoft.framework.kube.kts.core.builder

import org.pcsoft.framework.kube.kts.core.KubeHelmRepository
import org.pcsoft.framework.kube.kts.core.KubeKtsRepository
import java.nio.file.Path

interface KubeKtsRepositoryBuilder {
    companion object {
        val DEFAULT: KubeKtsRepositoryBuilder = DefaultKubeKtsRepositoryBuilder()
    }

    fun build(repository: KubeKtsRepository, valueFiles: Array<Path>): KubeHelmRepository
}