package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.successStyle
import org.pcsoft.framework.kube.kts.logging.symbolMainProcess
import picocli.CommandLine.Command

@Command(name = "validate", description = ["Validate a KTS based chart repository"])
class ValidateCommand : BaseRootCommand() {
    companion object {
        private val logger = logger()
    }

    override fun run() {
        logger.atInfo().log { "$symbolMainProcess Start scanning repository at $sourcePath" }
        KubeKtsRepositoryScanner.DEFAULT.scan(sourcePath)
        logger.atInfo().log { "Repository scanned".successStyle() }
    }
}