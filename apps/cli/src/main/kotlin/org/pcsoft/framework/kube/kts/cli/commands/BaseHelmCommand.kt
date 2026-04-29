package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.logging.*


sealed class BaseHelmCommand : BaseRenderCommand() {
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
        logger.atInfo().log { "$symbolMainProcess Run helm..." }
        logger.atDebug().log { "$symbolBullet with arguments: ${helmArguments.joinToString(" ")}" }
        
        logger.atDebug().log { "$symbolBullet Start process..." }
        val args = arrayOf(
            *helmArguments,
            *values.flatMap { listOf("-f", it.toString()) }.toTypedArray()
        )
        logger.atTrace().log { "\t$symbolArrowRight Arguments: ${args.joinToString(" ")}" }
        val process = ProcessBuilder()
            .command("helm", *args)
            .directory(targetPath.toFile())
            .start()
        logger.atTrace().log { "$symbolArrowRight Process started" }

        process.inputStream.bufferedReader().use { reader ->
            reader.lines().forEach { line ->
                logger.atInfo().log { "[HELM EXECUTION]".subProcessTitleStyle() + " ${line.subProcessInfoStyle()}" }
            }
        }

        process.errorStream.bufferedReader().use { reader ->
            reader.lines().forEach { line ->
                logger.atError().log { "[HELM EXECUTION]".subProcessTitleStyle() + " $line".subProcessErrorStyle() }
            }
        }

        logger.atTrace().log { "$symbolBullet Wait for process to finish..." }
        val exitCode = process.waitFor()
        logger.atDebug().log { "$symbolArrowRight Process finished with exit code $exitCode" }

        if (exitCode == 0) {
            logger.atInfo().log { "Helm command finished successfully".successStyle() }
        } else {
            logger.atError().log { "Helm command failed with exit code $exitCode".failedStyle() }
        }

        return exitCode
    }
}