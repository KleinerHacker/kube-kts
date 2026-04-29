package org.pcsoft.framework.kube.kts.cli.commands.flags

import org.pcsoft.framework.kube.kts.cli.intern.NoArgs
import picocli.CommandLine.Option
import java.nio.file.Path
import kotlin.io.path.exists

interface CompileFlags {
    val values: Array<Path>
}

@Suppress("ArrayInDataClass")
@NoArgs
data class CompileFlagsImpl(
    @field:Option(names = ["-f", "--values"], description = ["Value files to use while running Kotlin Scripts"], paramLabel = "FILE")
    private val rawValues: Array<String> = emptyArray()
) : CompileFlags {
    override val values: Array<Path> by lazy {
        rawValues.map {
            Path.of(it).apply {
                require(exists()) { "Value file does not exist: $it" }
            }
        }.toTypedArray()
    }
}