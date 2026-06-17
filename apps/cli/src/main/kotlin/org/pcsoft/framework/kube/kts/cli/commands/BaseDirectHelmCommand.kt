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

import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.pcsoft.framework.kube.kts.cli.commands.helm.HelmArgsProvider
import org.pcsoft.framework.kube.kts.cli.commands.helm.HelmCommandLineProvider
import org.pcsoft.framework.kube.kts.cli.intern.logging.KubeKtsConsoleAppender
import org.pcsoft.framework.kube.kts.logging.*
import java.nio.file.Path
import java.util.concurrent.Callable

/**
 * Base class for Helm commands that operate on an already installed release (e.g. `status`, `list`,
 * `history`).
 *
 * Unlike [BaseRenderedHelmCommand], this class does **not** derive from [BaseRenderCommand]: it neither
 * requires a `REPOSITORY` positional nor runs the *Scan → Compile → Render* pipeline. The KTS scripts
 * are irrelevant for these commands, so the assembled command line is forwarded directly to `helm`.
 *
 * The `call()` boilerplate (logging setup, flag validation, exception handling) is intentionally
 * duplicated from [BaseRootCommand] instead of inherited, because [BaseRootCommand] enforces a
 * mandatory `REPOSITORY` positional that release commands must not have.
 *
 * Subclasses define the Helm subcommand and positionals via [helmCommand] and contribute their
 * option groups via [helmOptionGroups].
 */
sealed class BaseDirectHelmCommand : BaseCommand(), Callable<Int>, HelmCommandLineProvider {
    companion object {
        private val logger = logger()

        /**
         * The executor used to run Helm. Defaults to the real process executor; tests may swap it for
         * a mock to capture the forwarded arguments. Always reset it afterwards.
         */
        internal var helmExecutor: HelmExecutor = ProcessHelmExecutor
    }

    /**
     * The Helm subcommand together with its positional arguments, e.g. `["status", "my-release"]`.
     *
     * Subclasses must provide the leading subcommand and any positionals; all flag options are added
     * separately through [helmOptionGroups].
     */
    protected abstract val helmCommand: List<String>

    /**
     * The option groups (picocli mixins implementing [HelmArgsProvider]) contributing flags to Helm.
     *
     * The global `--debug` flag is appended automatically by [buildHelmCommandLine] and must not be
     * repeated here.
     */
    protected abstract val helmOptionGroups: List<HelmArgsProvider>

    /**
     * Assembles the complete argument list passed to the `helm` executable.
     *
     * The result is composed of [helmCommand], the flags from every group in [helmOptionGroups], and
     * `--debug` when debug mode is active. Only options actually set by the user contribute arguments.
     *
     * @return the ordered list of Helm CLI arguments.
     */
    override fun buildHelmCommandLine(): List<String> = buildList {
        addAll(helmCommand)
        helmOptionGroups.forEach { addAll(it.toHelmArgs()) }
        if (isDebug) add("--debug")
    }

    /**
     * Executes the command: configures logging, validates global flags, runs Helm and maps the result
     * to an exit code (0 on success, 1 on failure).
     *
     * @return Exit code: 0 for success, 1 for failure.
     */
    final override fun call(): Int {
        val level = if (isTrace) Level.TRACE else if (isDebug) Level.DEBUG else Level.INFO
        LogManager.getRootLogger().level = level
        LogManager.getRootLogger().allAppenders.toList()
            .filterIsInstance<KubeKtsConsoleAppender>()
            .forEach { it.showLogLevel = showLogLevel }

        return try {
            validateGlobalFlags()

            val exitCode = runHelm()
            if (exitCode != 0)
                throw IllegalStateException("Helm command failed with exit code $exitCode")

            0
        } catch (e: Exception) {
            if (exception)
                logger.error(e.message?.failedStyle(), e)
            else
                logger.error(e.message?.failedStyle())
            1
        }
    }

    private fun runHelm(): Int {
        val args = buildHelmCommandLine()
        logger.atInfo().log { "$symbolMainProcess Run helm..." }
        logger.atDebug().log { "$symbolBullet with arguments: ${args.joinToString(" ")}" }

        logger.atDebug().log { "$symbolBullet Start process..." }
        logger.atTrace().log { "\t$symbolArrowRight Arguments: ${args.joinToString(" ")}" }
        val exitCode = helmExecutor.execute(args, Path.of(""))
        logger.atDebug().log { "$symbolArrowRight Process finished with exit code $exitCode" }

        if (exitCode == 0) {
            logger.atInfo().log { "Helm command finished successfully".successStyle() }
        } else {
            logger.atError().log { "Helm command failed with exit code $exitCode".failedStyle() }
        }

        return exitCode
    }
}
