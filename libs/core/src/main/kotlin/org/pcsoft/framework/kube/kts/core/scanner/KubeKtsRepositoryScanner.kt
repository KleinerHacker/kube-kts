package org.pcsoft.framework.kube.kts.core.scanner

import org.pcsoft.framework.kube.kts.core.KubeKtsRepository
import java.nio.file.Path

interface KubeKtsRepositoryScanner {
    companion object {
        val DEFAULT: KubeKtsRepositoryScanner = DefaultKubeKtsRepositoryScanner
    }

    fun scan(path: Path): KubeKtsRepository
}