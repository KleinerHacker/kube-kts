/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
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

import org.pcsoft.framework.kube.kts.cli.commands.helm.HelmArgsProvider
import org.pcsoft.framework.kube.kts.logging.*
import java.nio.file.Path

/**
 * Runs the assembled Helm command line and returns the process exit code.
 *
 * The default implementation [ProcessHelmExecutor] spawns the real `helm` process. Tests may replace
 * [BaseHelmCommand.helmExecutor] with a mock to verify the forwarded arguments without invoking Helm.
 */
fun interface HelmExecutor {
    /**
     * Executes `helm` with [args] in [workingDir].
     *
     * @param args the Helm arguments (without the leading `helm` itself).
     * @param workingDir the directory the rendered Helm chart was written to.
     * @return the process exit code (`0` on success).
     */
    fun execute(args: List<String>, workingDir: Path): Int
}

/** Default [HelmExecutor] spawning the real `helm` process and streaming its output to the log. */
internal object ProcessHelmExecutor : HelmExecutor {
    private val logger = logger()

    override fun execute(args: List<String>, workingDir: Path): Int {
        val process = ProcessBuilder()
            .command("helm", *args.toTypedArray())
            .directory(workingDir.toFile())
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
        return process.waitFor()
    }
}

/**
 * Base class for Helm commands that extends [BaseRenderCommand].
 *
 * This sealed class provides the foundation for executing Helm operations by defining a common structure
 * and behavior. It handles the execution of Helm commands, logging their output, and managing process lifecycle.
 *
 * Subclasses define the Helm subcommand and positionals via [helmCommand] and contribute their option
 * groups via [helmOptionGroups]. The fully assembled command line is built by [buildHelmCommandLine].
 */
sealed class BaseHelmCommand : BaseRenderCommand() {
    companion object {
        private val logger = logger()

        /**
         * The executor used to run Helm. Defaults to the real process executor; tests may swap it for
         * a mock to capture the forwarded arguments. Always reset it afterwards.
         */
        internal var helmExecutor: HelmExecutor = ProcessHelmExecutor
    }

    /**
     * The Helm subcommand together with its positional arguments, e.g. `["install", "my-release", "."]`.
     *
     * Subclasses must provide the leading subcommand and any positionals; all flag options are added
     * separately through [helmOptionGroups] and the shared value/debug forwarding.
     */
    protected abstract val helmCommand: List<String>

    /**
     * The option groups (picocli mixins implementing [HelmArgsProvider]) contributing flags to Helm.
     *
     * Each command lists the mixins it embeds. The shared `-f`/`--values` files and the global
     * `--debug` flag are appended automatically by [buildHelmCommandLine] and must not be repeated here.
     */
    protected abstract val helmOptionGroups: List<HelmArgsProvider>

    /**
     * Assembles the complete argument list passed to the `helm` executable.
     *
     * The result is composed of [helmCommand], the flags from every group in [helmOptionGroups], the
     * shared `-f`/`--values` files, and `--debug` when debug mode is active. Only options actually set
     * by the user contribute arguments. Exposed with `internal` visibility so it can be verified by
     * tests without invoking Helm.
     *
     * @return the ordered list of Helm CLI arguments.
     */
    internal fun buildHelmCommandLine(): List<String> = buildList {
        addAll(helmCommand)
        helmOptionGroups.forEach { addAll(it.toHelmArgs()) }
        addAll(valueFileOptions.toHelmArgs())
        if (isDebug) add("--debug")
    }

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

        val exitCode = runHelm()
        if (exitCode != 0)
            throw IllegalStateException("Helm command failed with exit code $exitCode")
    }

    private fun runHelm(): Int {
        val args = buildHelmCommandLine()
        logger.atInfo().log { "$symbolMainProcess Run helm..." }
        logger.atDebug().log { "$symbolBullet with arguments: ${args.joinToString(" ")}" }

        logger.atDebug().log { "$symbolBullet Start process..." }
        logger.atTrace().log { "\t$symbolArrowRight Arguments: ${args.joinToString(" ")}" }
        val exitCode = helmExecutor.execute(args, targetPath)
        logger.atDebug().log { "$symbolArrowRight Process finished with exit code $exitCode" }

        if (exitCode == 0) {
            logger.atInfo().log { "Helm command finished successfully".successStyle() }
        } else {
            logger.atError().log { "Helm command failed with exit code $exitCode".failedStyle() }
        }

        return exitCode
    }
}