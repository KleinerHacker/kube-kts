package org.pcsoft.framework.kube.kts.cli.commands

import picocli.CommandLine.Command

@Command(name = "lint", description = ["Linting a KTS based chart repository with helm"])
object LintCommand : HelmCommand() {
    override fun runKbeKts() {

    }

}