package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.cli.intern.utils.blue
import org.pcsoft.framework.kube.kts.cli.intern.utils.gray
import org.pcsoft.framework.kube.kts.cli.intern.utils.green
import org.pcsoft.framework.kube.kts.cli.intern.utils.logger
import org.pcsoft.framework.kube.kts.cli.intern.utils.red

sealed class HelmCommand : KubeKtsCommand() {
    companion object {
        private val logger = logger()
    }
    
    protected abstract val helmArguments: Array<String>

    final override fun run() {
        super.run()

        if (runHelm() != 0)
            throw IllegalStateException("Helm command failed with exit code ${runHelm()}")
    }

    private fun runHelm(): Int {
        logger.atInfo().log { "Run helm..." }
        logger.atDebug().log { "> with arguments: ${helmArguments.joinToString(" ")}" }
        
        logger.atDebug().log { "> Start process...".gray() }
        val process = ProcessBuilder()
            .command("helm", *helmArguments)
            .directory(usedTargetPath.toFile())
            .start()
        logger.atTrace().log { "> Process started".gray() }

        process.inputStream.bufferedReader().use { reader ->
            reader.lines().forEach { line ->
                logger.atInfo().log { "[HELM EXECUTION]".blue() + " $line" }
            }
        }

        process.errorStream.bufferedReader().use { reader ->
            reader.lines().forEach { line ->
                logger.atError().log { "[HELM EXECUTION]".blue() + " $line".red() }
            }
        }

        logger.atTrace().log { "> Wait for process to finish...".gray() }
        val exitCode = process.waitFor()
        logger.atDebug().log { "> Process finished with exit code $exitCode" }

        if (exitCode == 0) {
            logger.atInfo().log { "Helm command finished successfully".green() }
        }

        return exitCode
    }
}