/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.cli

import org.pcsoft.framework.kube.kts.cli.intern.utils.HelmHelpFactory
import picocli.CommandLine

fun main(args: Array<String>) {
    runCli(args)
}

/**
 * Runs the CLI application with the specified command-line arguments.
 *
 * This function sets up a [CommandLine] instance for the [MainCommand], configures its properties such as
 * command name, color scheme, and usage help width, and then executes the provided arguments. If no arguments are given,
 * it displays the usage information and returns 0.
 *
 * @param args The command-line arguments to be processed by the CLI application.
 * @return The exit code returned by the CLI execution. Returns 0 if no arguments were provided or if the help was displayed.
 */
fun runCli(args: Array<String>): Int {
    val commandLine = CommandLine(MainCommand).apply {
        commandName = "kube-kts"
        colorScheme = CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.ON)
        usageHelpWidth = 120
        usageHelpLongOptionsMaxWidth = 80
        isCaseInsensitiveEnumValuesAllowed = true
        helpFactory = HelmHelpFactory()
    }

    if (args.isEmpty()) {
        commandLine.usage(System.out)
        return 0
    }

    return commandLine.execute(*args)
}