package org.pcsoft.framework.kube.kts.cli.commands

import picocli.CommandLine
import picocli.CommandLine.Command

@Command(name = "template", description = ["Print template of a KTS based chart repository with helm"])
object TemplateCommand : HelmCommand() {
    @CommandLine.Option(names = ["-n", "--name"], description = ["Name of chart"], required = true)
    private lateinit var name: String

    override val helmArguments: Array<String>
        get() = arrayOf("template", name, ".")
}