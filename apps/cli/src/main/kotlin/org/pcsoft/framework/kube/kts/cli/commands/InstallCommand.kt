package org.pcsoft.framework.kube.kts.cli.commands

import picocli.CommandLine.Command

@Command(name = "install", description = ["Install a KTS based chart repository with helm"])
class InstallCommand : BaseHelmCommand() {
    override val helmArguments: Array<String>
        get() = arrayOf("install") //TODO
}