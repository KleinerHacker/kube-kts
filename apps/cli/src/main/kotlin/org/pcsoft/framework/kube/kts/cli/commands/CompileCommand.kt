package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.successStyle
import org.pcsoft.framework.kube.kts.logging.symbolMainProcess
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "compile", description = ["Compile a KTS based chart repository"])
object CompileCommand : BaseCommand() {
    private val logger = logger()

    @Parameters(index = "0", description = ["Path to the repository"])
    private lateinit var path: String

    override fun run() {
        logger.atInfo().log { "$symbolMainProcess Start scanning repository at $path" }
        val repository = KubeKtsRepositoryScanner.DEFAULT.scan(Path.of(path))
        logger.atInfo().log { "Repository scanned".successStyle() }

        logger.atInfo().log { "$symbolMainProcess Start compiling Helm repository from Kube Kts repository: ${repository.name}" }
        KubeKtsRepositoryBuilder.DEFAULT.build(repository)
        logger.atInfo().log { "Helm repository compiled".successStyle() }
    }
}