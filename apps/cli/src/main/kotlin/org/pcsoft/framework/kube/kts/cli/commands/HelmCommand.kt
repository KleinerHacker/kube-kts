package org.pcsoft.framework.kube.kts.cli.commands

sealed class HelmCommand : KubeKtsCommand() {
    protected abstract val helmArguments: Array<String>

    final override fun run() {
        super.run()

        if (runHelm() != 0)
            throw IllegalStateException("Helm command failed with exit code ${runHelm()}")
    }

    private fun runHelm(): Int {
        val process = ProcessBuilder()
            .command("helm", *helmArguments)
            .directory(usedTargetPath.toFile())
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()

        val exitCode = process.waitFor()

        return exitCode
    }
}