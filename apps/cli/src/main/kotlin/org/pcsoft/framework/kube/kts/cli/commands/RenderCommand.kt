package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.renderer.KubeHelmRepositoryRenderer
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "render", description = ["Render a KTS based chart repository to YAML"])
object RenderCommand : Runnable {
    @Parameters(index = "0", description = ["Path to the KTS repository"])
    private lateinit var sourcePath: String

    @Parameters(index = "1", description = ["Path to the YAML repository to create"])
    private lateinit var targetPath: String

    override fun run() {
        val ktsRepo = KubeKtsRepositoryScanner.DEFAULT.scan(Path.of(sourcePath))
        val helmRepo = KubeKtsRepositoryBuilder.DEFAULT.build(ktsRepo)
        KubeHelmRepositoryRenderer.DEFAULT.render(helmRepo, Path.of(targetPath))
    }
}