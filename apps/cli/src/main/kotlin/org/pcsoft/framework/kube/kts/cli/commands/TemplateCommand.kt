package org.pcsoft.framework.kube.kts.cli.commands

import picocli.CommandLine.Command

@Command(name = "template", description = ["Print template of a KTS based chart repository with helm"])
object TemplateCommand : HelmCommand() {
    override fun runKbeKts() {

    }

}