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

import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.successStyle
import org.pcsoft.framework.kube.kts.logging.symbolMainProcess
import picocli.CommandLine.Command

/**
 * Command to validate a KTS-based chart repository.
 *
 * This command scans the specified repository path for Kubernetes Kotlin Script (KTS) files and legacy Helm templates,
 * organizing them into a structured repository. It provides feedback on the scanning process through logging,
 * indicating when the scan starts and completes successfully.
 */
@Command(name = "validate", description = ["Validate a KTS based chart repository"])
class ValidateCommand : BaseRootCommand() {
    companion object {
        private val logger = logger()
    }

    /**
     * Executes the repository scanning process by logging the start of the scan,
     * invoking the [KubeKtsRepositoryScanner] to analyze the specified source path,
     * and logging a success message upon completion.
     */
    override fun run() {
        logger.atInfo().log { "$symbolMainProcess Start scanning repository at $sourcePath" }
        KubeKtsRepositoryScanner.DEFAULT.scan(sourcePath)
        logger.atInfo().log { "Repository scanned".successStyle() }
    }
}