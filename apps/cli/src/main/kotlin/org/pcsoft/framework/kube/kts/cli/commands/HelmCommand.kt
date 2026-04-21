package org.pcsoft.framework.kube.kts.cli.commands

import picocli.CommandLine.Parameters

sealed class HelmCommand : BaseCommand() {
    @Parameters(index = "0", description = ["Path to the KTS repository"])
    protected lateinit var sourcePath: String private set

    @Parameters(index = "1", description = ["Path to the YAML repository to create"])
    protected lateinit var targetPath: String private set

    final override fun run() {
        runKbeKts()
        runHelm()
    }

    private fun runHelm() {

    }

    protected abstract fun runKbeKts()
}