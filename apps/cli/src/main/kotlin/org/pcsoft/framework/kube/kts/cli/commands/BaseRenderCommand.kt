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

sealed class BaseRenderCommand : BaseCompileCommand() {
    companion object {
        private val logger = logger()
    }

    @Parameters(index = "1", description = ["Path to the YAML repository to create"], arity = "0..1")
    private var rawTargetPath: String? = null

    protected val targetPath: Path by lazy {
        rawTargetPath?.let { Path.of(it) } ?: Files.createTempDirectory("helm")
    }

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