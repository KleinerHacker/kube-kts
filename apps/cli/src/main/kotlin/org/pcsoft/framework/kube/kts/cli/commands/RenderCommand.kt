package org.pcsoft.framework.kube.kts.cli.commands

import picocli.CommandLine.Command

@Command(name = "render", description = ["Render a KTS based chart repository to YAML"])
class RenderCommand : BaseRenderCommand()