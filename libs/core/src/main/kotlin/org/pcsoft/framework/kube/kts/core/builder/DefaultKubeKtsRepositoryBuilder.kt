/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import org.pcsoft.framework.kube.kts.core.*
import org.pcsoft.framework.kube.kts.core.intern.utils.map
import org.pcsoft.framework.kube.kts.core.intern.utils.thenCollect
import org.pcsoft.framework.kube.kts.core.intern.utils.thenMap
import org.pcsoft.framework.kube.kts.core.intern.utils.thenMapWithError
import org.pcsoft.framework.kube.kts.core.intern.utils.withTempFileHandler
import org.pcsoft.framework.kube.kts.core.merge.YamlMerging
import org.pcsoft.framework.kube.kts.logging.*
import tools.jackson.dataformat.yaml.YAMLMapper
import java.nio.file.Files
import java.nio.file.Path

/**
 * Implementation of the `KubeKtsRepositoryBuilder` interface responsible for building
 * a Helm repository from a given KubeKTS repository configuration.
 *
 * This builder processes KubeKTS files, applies YAML merging strategies for value files,
 * and compiles and executes scripts to generate Helm-compatible files. The result is a
 * constructed Helm repository that includes Helm-specific files and any legacy files.
 *
 * @property processor The `KotlinScriptProcessor` used to compile and execute Kotlin scripts.
 * @property merging The `YamlMerging` strategy to merge multiple YAML value files into a unified structure.
 * @property unsafeMode Indicates if unsafe mode is enabled during script processing and execution.
 * @property helmFileMapper A functional mapping that transforms a `KubeKtsFile` and its
 * corresponding `KubeSpec` into a `KubeHelmFile`.
 */
internal class DefaultKubeKtsRepositoryBuilder(
    val processor: KotlinScriptProcessor,
    val merging: YamlMerging,
    val unsafeMode: Boolean,
    private val helmFileMapper: (KubeKtsFile, KubeSpec) -> KubeHelmFile
) : KubeKtsRepositoryBuilder {
    companion object {
        private val logger = logger()
    }

    override fun build(repository: KubeKtsRepository, valueFiles: Array<Path>): KubeHelmRepository {
        logger.atDebug().log { "$symbolProcess Building Helm repository from Kube KTS repository: ${repository.name}" }

        return withTempFileHandler {
            logger.atDebug().log { "$symbolBullet Merge ${valueFiles.size} value files..." }
            logger.atTrace().log { "\t$symbolArrowRight ${valueFiles.joinToString(", ") { it.fileName.toString() }}" }
            val baseValue = repository.legacyFiles.firstOrNull { it.isValues }?.let {
                Files.createTempFile("base", ".${it.extension}").also { path ->
                    Files.writeString(path, it.content, Charsets.UTF_8)
                }.toTempFile()
            }

            val mergedValueFile = merging.merge(baseValue, *valueFiles)
            val valueNode = mergedValueFile?.let { YAMLMapper().readTree(it) } ?: YAMLMapper().createObjectNode()
            val valueAccess = ValueAccess.ofRoot(valueNode)

            logger.atDebug().log { "$symbolBullet Create ${repository.libFiles.size} lib script temp files..." }
            logger.atTrace().log { "\t$symbolArrowRight ${repository.libFiles.joinToString(", ") { it.subject }}" }
            val libTempFiles = repository.libFiles.map { libFile ->
                Files.createTempFile(libFile.subject, ".lib.kts").also { path ->
                    Files.writeString(path, libFile.script, Charsets.UTF_8)
                }.toTempFile()
            }

            logger.atDebug().log { "$symbolBullet Build ${repository.specFiles.size} files..." }
            logger.atTrace().log {
                "\t$symbolArrowRight ${repository.specFiles.joinToString(", ") { it.subject }}"
            }

            val helmFiles = repository.specFiles
                .map { file ->
                    withTempFile(file) {
                        processor.compile(file.subject, it, libTempFiles, unsafeMode)
                            .map { file to it }
                    }
                }
                .thenMapWithError { pair ->
                    processor.execute<KubeSpec>(pair.first.subject, pair.second, valueAccess)
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
            KubeHelmRepository(
                repository.name,
                (helmFiles as Either.Success<Iterable<KubeHelmFile>>).value.toList(),
                repository.legacyFiles
            )
        }
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