package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.renderer.KubeHelmRepositoryRenderer
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.successStyle
import org.pcsoft.framework.kube.kts.logging.symbolMainProcess
import picocli.CommandLine.Parameters
import java.nio.file.Files
import java.nio.file.Path

sealed class KubeKtsCommand : BaseCommand() {
    companion object {
        private val logger = logger()
    }

    @Parameters(index = "0", description = ["Path to the KTS repository"])
    protected lateinit var sourcePath: String private set

    @Parameters(index = "1", description = ["Path to the YAML repository to create"], arity = "0..1")
    protected var targetPath: String? = null; private set

    protected val usedTargetPath: Path by lazy {
        if (targetPath != null) Path.of(targetPath!!) else Files.createTempDirectory("helm")
    }

    override fun run() {
        logger.atInfo().log { "$symbolMainProcess Start scanning repository at $sourcePath" }
        val ktsRepo = KubeKtsRepositoryScanner.DEFAULT.scan(Path.of(sourcePath))
        logger.atInfo().log { "Repository scanned".successStyle() }

        logger.atInfo().log { "$symbolMainProcess Start compiling Helm repository from Kube Kts repository: ${ktsRepo.name}" }
        val helmRepo = KubeKtsRepositoryBuilder.DEFAULT.build(ktsRepo)
        logger.atInfo().log { "Helm repository compiled".successStyle() }

        logger.atInfo().log { "$symbolMainProcess Start rendering Helm repository to $usedTargetPath" }
        KubeHelmRepositoryRenderer.DEFAULT.render(helmRepo, usedTargetPath)
        logger.atInfo().log { "Helm repository rendered to $usedTargetPath".successStyle() }
    }
}