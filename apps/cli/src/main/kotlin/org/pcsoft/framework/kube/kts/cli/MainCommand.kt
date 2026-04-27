package org.pcsoft.framework.kube.kts.cli

import org.pcsoft.framework.kube.kts.cli.commands.*
import picocli.CommandLine.*

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
        "A Helm wrapper for managing Kubernetes deployments using Kotlin-based chart repositories.",
        "Compiles, renders, and executes Kotlin Script (KTS) Helm charts with full Helm compatibility.",
        ""
    ],
    version = ["Kube KTS 0.1.0"],
    subcommandsRepeatable = false,
    commandListHeading = "%n@|bold Commands|@%n",
)
object MainCommand {
    @Mixin
    lateinit var globalFlags: GlobalFlags
}