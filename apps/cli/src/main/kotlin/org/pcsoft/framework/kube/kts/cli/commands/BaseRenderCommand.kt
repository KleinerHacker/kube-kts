/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.cli.types.YamlMergingAlgorithm
import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.merge.YamlMerging
import org.pcsoft.framework.kube.kts.core.renderer.KubeHelmRepositoryRenderer
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.successStyle
import org.pcsoft.framework.kube.kts.logging.symbolMainProcess
import picocli.CommandLine.Parameters
import java.nio.file.Files
import java.nio.file.Path

/**
 * Base class for render commands that process a Kubernetes Kotlin Script (KTS) repository,
 * compile it into a Helm repository, and render the output to a specified directory.
 *
 * This sealed class extends [BaseCompileCommand] and handles the scanning of KTS files,
 * compilation into Helm format using configurable YAML merging strategies, and rendering
 * the final Helm repository to either a user-specified or temporary directory. The process
 * includes logging each major step with appropriate status messages.
 */
sealed class BaseRenderCommand : BaseCompileCommand() {
    companion object {
        private val logger = logger()
    }

    @Parameters(index = "1", description = ["Path to the YAML repository to create"], arity = "0..1")
    private var rawTargetPath: String? = null

    /**
     * The target directory path where rendered Helm charts will be stored.
     *
     * This property is lazily initialized. If [rawTargetPath] is provided, it creates a [Path]
     * from that value. Otherwise, it creates a temporary directory with the prefix "helm".
     */
    protected val targetPath: Path by lazy {
        rawTargetPath?.let { Path.of(it) } ?: Files.createTempDirectory("helm")
    }

    /**
     * Executes the main rendering process for converting a Kubernetes Kotlin Script (KTS) repository into a Helm
     * repository and rendering it to the target directory.
     *
     * This method performs the following steps:
     * 1. Scans the source path for KTS files and legacy Helm templates, creating a [KubeKtsRepository].
     * 2. Compiles the scanned repository into a Helm repository using the specified YAML merging algorithm and values.
     * 3. Renders the compiled Helm repository to the target directory.
     *
     * The process logs informational messages at each stage, including success indicators for completed operations.
     */
    override fun run() {
        logger.atInfo().log { "$symbolMainProcess Start scanning repository at $sourcePath" }
        val ktsRepo = KubeKtsRepositoryScanner.DEFAULT.scan(sourcePath)
        logger.atInfo().log { "Repository scanned".successStyle() }

        logger.atInfo()
            .log { "$symbolMainProcess Start compiling Helm repository from Kube Kts repository: ${ktsRepo.name}" }
        val yamlMerging = when (yamlMergeAlgorithm) {
            YamlMergingAlgorithm.INTERNAL -> YamlMerging.createDefault(yamlArrayMergeStrategy)
            YamlMergingAlgorithm.HELM -> YamlMerging.HELM
        }
        val helmRepo = KubeKtsRepositoryBuilder
            .createDefault(unsafe = unsafeMode, merging = yamlMerging)
            .build(ktsRepo, values)
        logger.atInfo().log { "Helm repository compiled".successStyle() }

        logger.atInfo().log { "$symbolMainProcess Start rendering Helm repository to $targetPath" }
        KubeHelmRepositoryRenderer.DEFAULT.render(helmRepo, targetPath)
        logger.atInfo().log { "Helm repository rendered to $targetPath".successStyle() }
    }
}