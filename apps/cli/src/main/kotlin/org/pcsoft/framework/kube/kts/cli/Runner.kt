package org.pcsoft.framework.kube.kts.cli

import org.pcsoft.framework.kube.kts.cli.commands.*
import org.pcsoft.framework.kube.kts.cli.intern.NoArgs
import picocli.CommandLine
import picocli.CommandLine.*

fun main(args: Array<String>) {
    runCli(args)
}

fun runCli(args: Array<String>): Int {
    val commandLine = CommandLine(MainCommand)

    if (args.isEmpty()) {
        commandLine.usage(System.out)
        return 0
    }

    return commandLine.execute(*args)
}

@Command(
    subcommands = [
        HelpCommand::class,
        ValidateCommand::class,
        CompileCommand::class,
        RenderCommand::class,
        LintCommand::class,
        TemplateCommand::class,
        InstallCommand::class,
        UninstallCommand::class
    ],
    header = ["Kube KTS 0.1.0 - 2026", ""],
    description = [
        "Wrapper for helm to use KTS based helm repositories.",
        "This tool compile, render and run with helm these Kotlin Script 'helm' repository",
        ""
    ],
    version = ["Kube KTS 0.1.0"],
    subcommandsRepeatable = false,
)
object MainCommand {
    @Mixin
    lateinit var globalFlags: GlobalFlags
}

@NoArgs
class GlobalFlags(
    @field:Option(names = ["-d", "--debug"], description = ["Print debug information"])
    var debug: Boolean,
    @field:Option(names = ["-v", "--verbose"], description = ["Print all information"])
    var verbose: Boolean,
    @field:Option(names = ["-e", "--exception"], description = ["Print exceptions in case of errors"])
    var exception: Boolean
)