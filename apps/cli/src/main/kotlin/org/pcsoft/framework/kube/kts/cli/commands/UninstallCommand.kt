package org.pcsoft.framework.kube.kts.cli.commands

import picocli.CommandLine.Command

@Command(name = "uninstall", description = ["Uninstall a KTS based chart repository with helm"])
object UninstallCommand : HelmCommand() {
    override fun runKbeKts() {

    }
}