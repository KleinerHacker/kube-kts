package org.pcsoft.framework.kube.kts.core.builder

import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import org.pcsoft.framework.kube.kts.core.DefaultKubeHelmFile
import org.pcsoft.framework.kube.kts.core.KubeHelmFile
import org.pcsoft.framework.kube.kts.core.KubeHelmRepository
import org.pcsoft.framework.kube.kts.core.KubeKtsFile
import org.pcsoft.framework.kube.kts.core.KubeKtsRepository
import org.pcsoft.framework.kube.kts.core.merge.YamlMerging
import java.nio.file.Path

interface KubeKtsRepositoryBuilder {
    companion object {
        fun createDefault(
            processor: KotlinScriptProcessor = KotlinScriptProcessor.DEFAULT,
            merging: YamlMerging = YamlMerging.HELM,
            unsafe: Boolean = false,
            helmFileMapper: (KubeKtsFile, KubeSpec) -> KubeHelmFile = { file, spec ->
                DefaultKubeHelmFile(file, spec)
            }
        ): KubeKtsRepositoryBuilder =
            DefaultKubeKtsRepositoryBuilder(processor, merging, unsafe, helmFileMapper)
    }

    fun build(repository: KubeKtsRepository, valueFiles: Array<Path>): KubeHelmRepository
}