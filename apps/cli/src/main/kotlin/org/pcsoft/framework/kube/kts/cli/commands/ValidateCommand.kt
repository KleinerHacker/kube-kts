package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.successStyle
import org.pcsoft.framework.kube.kts.logging.symbolMainProcess
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "validate", description = ["Validate a KTS based chart repository"])
object ValidateCommand : BaseCommand() {
    private val logger = logger()

    @Parameters(index = "0", description = ["Path to the repository"])
    private lateinit var path: String

    override fun run() {
        logger.atInfo().log { "$symbolMainProcess Start scanning repository at $path" }
        KubeKtsRepositoryScanner.DEFAULT.scan(Path.of(path))
        logger.atInfo().log { "Repository scanned".successStyle() }
    }
}