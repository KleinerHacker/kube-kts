/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.logging.*

/**
 * Base class for Helm commands that extends [BaseRenderCommand].
 *
 * This sealed class provides the foundation for executing Helm operations by defining a common structure
 * and behavior. It handles the execution of Helm commands, logging their output, and managing process lifecycle.
 *
 * Subclasses must implement [helmArguments] to specify the arguments required for the specific Helm command.
 */
sealed class BaseHelmCommand : BaseRenderCommand() {
    companion object {
        private val logger = logger()
    }

    /**
     * Array of command-line arguments to be passed to the Helm CLI.
     *
     * This property defines the specific Helm subcommand and its arguments that will be executed
     * when running this command. Subclasses must override this property to provide the appropriate
     * Helm command structure for their specific functionality.
     */
    protected abstract val helmArguments: Array<String>

    /**
     * Executes the Helm command and verifies its success.
     *
     * This method first calls the parent class's [run] implementation, then executes a Helm command
     * using [runHelm]. If the Helm command fails (returns a non-zero exit code), an [IllegalStateException]
     * is thrown with details about the failure. The exception includes the exit code from the failed command.
     *
     * @throws IllegalStateException if the Helm command execution fails (non-zero exit code).
     */
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