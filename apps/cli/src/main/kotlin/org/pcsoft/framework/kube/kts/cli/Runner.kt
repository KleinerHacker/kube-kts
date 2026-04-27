package org.pcsoft.framework.kube.kts.cli

import picocli.CommandLine

fun main(args: Array<String>) {
    runCli(args)
}

fun runCli(args: Array<String>): Int {
    val commandLine = CommandLine(MainCommand).apply {
        commandName = "kube-kts"
        colorScheme = CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.ON)
        usageHelpWidth = 120
    }

    if (args.isEmpty()) {
        commandLine.usage(System.out)
        return 0
    }

    return commandLine.execute(*args)
}