package org.pcsoft.framework.kube.kts.cli

import org.pcsoft.framework.kube.kts.cli.commands.CompileCommand
import org.pcsoft.framework.kube.kts.cli.commands.RenderCommand
import org.pcsoft.framework.kube.kts.cli.commands.ValidateCommand
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option

fun main(args: Array<String>) {
    CommandLine(MainCommand).execute(*args)
}

@Command(subcommands = [CommandLine.HelpCommand::class, ValidateCommand::class, CompileCommand::class, RenderCommand::class])
object MainCommand {
    @Option(names = ["-v", "--verbose"], description = ["Print debug information"])
    var verbose: Boolean = false
}