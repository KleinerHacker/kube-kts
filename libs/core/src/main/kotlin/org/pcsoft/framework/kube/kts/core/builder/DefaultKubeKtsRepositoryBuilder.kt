package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import org.pcsoft.framework.kube.kts.core.*
import org.pcsoft.framework.kube.kts.core.intern.utils.map
import org.pcsoft.framework.kube.kts.core.intern.utils.thenCollect
import org.pcsoft.framework.kube.kts.core.intern.utils.thenMap
import org.pcsoft.framework.kube.kts.core.intern.utils.thenMapWithError
import org.pcsoft.framework.kube.kts.logging.*
import java.nio.file.Files
import java.nio.file.Path

class DefaultKubeKtsRepositoryBuilder(
    val processor: KotlinScriptProcessor = KotlinScriptProcessor.DEFAULT,
    private val helmFileMapper: (KubeKtsFile, KubeSpec) -> KubeHelmFile = { file, spec ->
        DefaultKubeHelmFile(file, spec)
    }
) : KubeKtsRepositoryBuilder {
    companion object {
        private val logger = logger()
    }

    override fun build(repository: KubeKtsRepository): KubeHelmRepository {
        logger.atDebug().log { "$symbolProcess Building Helm repository from Kube KTS repository: ${repository.name}" }

        logger.atDebug().log { "$symbolBullet Build ${repository.files.size} files..." }
        logger.atTrace().log {
            "\t$symbolArrowRight ${repository.files.joinToString(", ") { it.subject }}"
        }

        val helmFiles = repository.files
            .map { file ->
                withTempFile(file) {
                    processor.compile(file.subject, it)
                        .map { file to it }
                }
            }
            .thenMapWithError { pair ->
                processor.execute<KubeSpec>(pair.first.subject, pair.second)
                    .map { pair.first to it }
            }
            .thenMap { helmFileMapper(it.first, it.second) }
            .thenCollect {
                val collectedReasons = it.joinToString(System.lineSeparator()) {
                    it.reason
                }

                Either.Error("Multiple errors occurred during processing: ${System.lineSeparator()}$collectedReasons")
            }

        require(helmFiles !is Either.Error) {
            val reason = (helmFiles as Either.Error).reason
            logger.atTrace().log { "Errors during processing: $reason".failedStyle() }

            reason
        }

        logger.atDebug().log { "Build Helm repository finished: ${repository.name}".successStyle() }
        return KubeHelmRepository(
            repository.name,
            (helmFiles as Either.Success<Iterable<KubeHelmFile>>).value.toList(),
            repository.legacyFiles
        )
    }

    private fun <T> withTempFile(file: KubeKtsFile, action: (Path) -> T): T {
        val tmpFile = Files.createTempFile(file.subject, ".kts")
        Files.writeString(tmpFile, file.script, Charsets.UTF_8)

        try {
            return action(tmpFile)
        } finally {
            Files.deleteIfExists(tmpFile)
        }
    }
}