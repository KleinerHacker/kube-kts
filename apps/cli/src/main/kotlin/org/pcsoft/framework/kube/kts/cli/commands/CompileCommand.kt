package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "compile", description = ["Compile a KTS based chart repository"])
object CompileCommand : BaseCommand() {
    @Parameters(index = "0", description = ["Path to the repository"])
    private lateinit var path: String

    override fun run() {
        val repository = KubeKtsRepositoryScanner.DEFAULT.scan(Path.of(path))
        KubeKtsRepositoryBuilder.DEFAULT.build(repository)
    }
}