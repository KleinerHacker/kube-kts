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

package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.cli.types.YamlMergingAlgorithm
import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.merge.YamlMerging
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.successStyle
import org.pcsoft.framework.kube.kts.logging.symbolMainProcess
import picocli.CommandLine.Command

/**
 * Command for compiling a KTS-based chart repository into a Helm-compatible format.
 *
 * This command scans the specified source path for Kubernetes Kotlin Script (KTS) files and legacy Helm templates,
 * then compiles them into a Helm repository structure. The compilation process supports different YAML merging strategies
 * and can apply values from external files during script execution.
 */
@Command(name = "compile", description = ["Compile a KTS based chart repository"])
class CompileCommand : BaseCompileCommand() {
    companion object {
        private val logger = logger()
    }

    /**
     * Executes the compilation process for converting a KTS-based chart repository into a Helm-compatible format.
     *
     * This method performs the following steps:
     * 1. Scans the specified source path to discover Kubernetes Kotlin Script (KTS) files and legacy Helm templates,
     *    creating a `KubeKtsRepository` structure.
     * 2. Configures the YAML merging strategy based on the selected algorithm ([YamlMergingAlgorithm.INTERNAL]
     *    or [YamlMergingAlgorithm.HELM]).
     * 3. Builds the Helm repository using the configured builder with the scanned repository and provided values,
     *    applying the specified unsafe mode and YAML array merge strategy.
     *
     * Log messages are emitted at each stage to indicate progress, including repository scanning completion
     * and successful compilation of the Helm repository.
     */
    override fun run() {
        logger.atInfo().log { "$symbolMainProcess Start scanning repository at $sourcePath" }
        val repository = KubeKtsRepositoryScanner.DEFAULT.scan(sourcePath)
        logger.atInfo().log { "Repository scanned".successStyle() }

        logger.atInfo().log { "$symbolMainProcess Start compiling Helm repository from Kube Kts repository: ${repository.name}" }
        val yamlMerging = when (yamlMergeAlgorithm) {
            YamlMergingAlgorithm.INTERNAL -> YamlMerging.createDefault(yamlArrayMergeStrategy)
            YamlMergingAlgorithm.HELM -> YamlMerging.HELM
        }
        KubeKtsRepositoryBuilder
            .createDefault(unsafe = unsafeMode, merging = yamlMerging)
            .build(repository, values)
        logger.atInfo().log { "Helm repository compiled".successStyle() }
    }
}