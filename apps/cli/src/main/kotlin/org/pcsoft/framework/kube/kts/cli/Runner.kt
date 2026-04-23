package org.pcsoft.framework.kube.kts.cli

import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.pcsoft.framework.kube.kts.cli.commands.CompileCommand
import org.pcsoft.framework.kube.kts.cli.commands.InstallCommand
import org.pcsoft.framework.kube.kts.cli.commands.LintCommand
import org.pcsoft.framework.kube.kts.cli.commands.RenderCommand
import org.pcsoft.framework.kube.kts.cli.commands.TemplateCommand
import org.pcsoft.framework.kube.kts.cli.commands.UninstallCommand
import org.pcsoft.framework.kube.kts.cli.commands.ValidateCommand
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Option

fun main(args: Array<String>) {
    runCli(args)
}

fun runCli(args: Array<String>): Int {
    val commandLine = CommandLine(MainCommand)
        .setExecutionStrategy(CommandLine.RunAll())

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
object MainCommand : Runnable {
    @Option(names = ["-v", "--verbose"], description = ["Print debug information"])
    var verbose: Boolean = false

    @Option(names = ["-e", "--exception"], description = ["Print exceptions in case of errors"])
    var exception: Boolean = false

    override fun run() {
        val level = if (verbose) Level.TRACE else Level.INFO
        LogManager.getRootLogger().level = level
    }
}