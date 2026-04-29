package org.pcsoft.framework.kube.kts.cli.commands

import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(name = "template", description = ["Print template of a KTS based chart repository with helm"])
class TemplateCommand : BaseHelmCommand() {
    @Option(names = ["-n", "--name"], description = ["Name of chart"], required = true)
    private lateinit var name: String

    override val helmArguments: Array<String>
        get() = arrayOf("template", name, ".")
}