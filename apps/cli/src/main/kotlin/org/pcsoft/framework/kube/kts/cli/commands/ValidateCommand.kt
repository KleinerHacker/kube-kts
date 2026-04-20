package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import picocli.CommandLine
import picocli.CommandLine.Command
import java.nio.file.Path

@Command(name = "validate", description = ["Validate a KTS based chart repository"])
object ValidateCommand : Runnable {
    @CommandLine.Parameters(index = "0", description = ["Path to the repository"])
    private lateinit var path: String

    override fun run() {
        KubeKtsRepositoryScanner.DEFAULT.scan(Path.of(path))
    }
}