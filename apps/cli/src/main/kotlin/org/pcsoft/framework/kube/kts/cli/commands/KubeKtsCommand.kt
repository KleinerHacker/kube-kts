package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.renderer.KubeHelmRepositoryRenderer
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import picocli.CommandLine.Parameters
import java.nio.file.Files
import java.nio.file.Path

sealed class KubeKtsCommand : BaseCommand() {
    @Parameters(index = "0", description = ["Path to the KTS repository"])
    protected lateinit var sourcePath: String private set

    @Parameters(index = "1", description = ["Path to the YAML repository to create"], arity = "0..1")
    protected var targetPath: String? = null; private set

    protected val usedTargetPath: Path =
        if (targetPath != null) Path.of(targetPath!!) else Files.createTempDirectory("helm")

    override fun run() {
        val ktsRepo = KubeKtsRepositoryScanner.DEFAULT.scan(Path.of(sourcePath))
        val helmRepo = KubeKtsRepositoryBuilder.DEFAULT.build(ktsRepo)

        KubeHelmRepositoryRenderer.DEFAULT.render(helmRepo, usedTargetPath)
        println("Rendered to: $usedTargetPath")
    }
}