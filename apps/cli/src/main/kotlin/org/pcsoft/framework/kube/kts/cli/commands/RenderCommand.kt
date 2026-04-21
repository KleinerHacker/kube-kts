package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.renderer.KubeHelmRepositoryRenderer
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Files
import java.nio.file.Path

@Command(name = "render", description = ["Render a KTS based chart repository to YAML"])
object RenderCommand : BaseCommand() {
    @Parameters(index = "0", description = ["Path to the KTS repository"])
    private lateinit var sourcePath: String

    @Parameters(index = "1", description = ["Path to the YAML repository to create"], arity = "0..1")
    private var targetPath: String? = null

    override fun run() {
        val ktsRepo = KubeKtsRepositoryScanner.DEFAULT.scan(Path.of(sourcePath))
        val helmRepo = KubeKtsRepositoryBuilder.DEFAULT.build(ktsRepo)
        val usedTargetPath = getTargetPath()

        KubeHelmRepositoryRenderer.DEFAULT.render(helmRepo, usedTargetPath)
        println("Rendered to: $usedTargetPath")
    }

    private fun getTargetPath() =
        if (targetPath != null) Path.of(targetPath!!) else Files.createTempDirectory("helm")
}