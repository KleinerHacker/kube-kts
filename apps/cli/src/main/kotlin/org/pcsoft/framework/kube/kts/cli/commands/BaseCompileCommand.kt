package org.pcsoft.framework.kube.kts.cli.commands

import picocli.CommandLine.Option
import java.nio.file.Path
import kotlin.io.path.exists

sealed class BaseCompileCommand : BaseRootCommand() {
    @Option(names = ["-f", "--values"], description = ["Values file to use while execute Kotlin Scripts"], paramLabel = "FILE")
    private var rawValues: Array<String>? = null

    protected val values: Array<Path> by lazy {
        rawValues?.map {
            Path.of(it).apply {
                require(exists()) { "Values file $it not found" }
            }
        }?.toTypedArray() ?: emptyArray()
    }
}