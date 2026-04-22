package org.pcsoft.framework.kube.kts.cli

import org.pcsoft.framework.kube.kts.cli.commands.CompileCommand
import org.pcsoft.framework.kube.kts.cli.commands.InstallCommand
import org.pcsoft.framework.kube.kts.cli.commands.LintCommand
import org.pcsoft.framework.kube.kts.cli.commands.RenderCommand
import org.pcsoft.framework.kube.kts.cli.commands.TemplateCommand
import org.pcsoft.framework.kube.kts.cli.commands.UninstallCommand
import org.pcsoft.framework.kube.kts.cli.commands.ValidateCommand
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option

fun main(args: Array<String>) {
    runCli(args)
}

fun runCli(args: Array<String>): Int {
    System.setProperty("org.slf4j.simpleLogger.showThreadName", "false")
    System.setProperty("org.slf4j.simpleLogger.showLogName", "false")
    System.setProperty("org.slf4j.simpleLogger.showShortLogName", "false")
    System.setProperty("org.slf4j.simpleLogger.levelInBrackets", "true")

    val commandLine = CommandLine(MainCommand)
    commandLine.parseArgs(*args)
    MainCommand.run()

    return commandLine.execute(*args)
}

@Command(
    subcommands = [
        CommandLine.HelpCommand::class,
        ValidateCommand::class,
        CompileCommand::class,
        RenderCommand::class,
        LintCommand::class,
        TemplateCommand::class,
        InstallCommand::class,
        UninstallCommand::class
    ]
)
object MainCommand : Runnable {
    @Option(names = ["-v", "--verbose"], description = ["Print debug information"])
    var verbose: Boolean = false
    @Option(names = ["-e", "--exception"], description = ["Print exceptions in case of errors"])
    var exception: Boolean = false

    override fun run() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", if (verbose) "trace" else "info")
    }
}