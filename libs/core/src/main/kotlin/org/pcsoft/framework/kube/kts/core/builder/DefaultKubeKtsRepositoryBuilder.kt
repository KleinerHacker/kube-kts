package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import org.pcsoft.framework.kube.kts.core.*
import org.pcsoft.framework.kube.kts.core.intern.utils.map
import org.pcsoft.framework.kube.kts.core.intern.utils.thenCollect
import org.pcsoft.framework.kube.kts.core.intern.utils.thenMap
import org.pcsoft.framework.kube.kts.core.intern.utils.thenMapWithError

class DefaultKubeKtsRepositoryBuilder(
    val processor: KotlinScriptProcessor = KotlinScriptProcessor.DEFAULT,
    private val helmFileMapper: (KubeKtsFile, KubeSpec) -> KubeHelmFile = { file, spec ->
        DefaultKubeHelmFile(file, spec)
    }
) : KubeKtsRepositoryBuilder {
    override fun build(repository: KubeKtsRepository): KubeHelmRepository {
        val helmFiles = repository.files
            .map { file -> processor.compile(file.script).map { file to it } }
            .thenMapWithError { pair -> processor.execute<KubeSpec>(pair.second).map { pair.first to it } }
            .thenMap { helmFileMapper(it.first, it.second) }
            .thenCollect {
                Either.Error(
                    "Multiple errors occurred during processing: " + System.lineSeparator() + it.joinToString(
                        System.lineSeparator()
                    )
                )
            }

        require(helmFiles !is Either.Error) { (helmFiles as Either.Error).reason }

        return KubeHelmRepository((helmFiles as Either.Success<Iterable<KubeHelmFile>>).value.toList())
    }
}