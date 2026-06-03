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
import org.pcsoft.framework.kube.kts.cli.intern.logging.KubeKtsConsoleAppender
import org.pcsoft.framework.kube.kts.logging.failedStyle
import org.pcsoft.framework.kube.kts.logging.logger
import picocli.CommandLine.Parameters
import java.nio.file.Path
import java.util.concurrent.Callable
import kotlin.io.path.exists

/**
 * Abstract base class for root-level commands that operate on a repository.
 *
 * This sealed class provides the foundation for commands that require access to a source repository path,
 * handling common functionality such as logging configuration, global flag validation, and exception handling.
 * Subclasses must implement the [run] method to define their specific behavior.
 */
sealed class BaseRootCommand : BaseCommand(), Callable<Int> {
    companion object {
        private val logger = logger()
    }

    @Parameters(index = "0", description = ["Path to the repository"], paramLabel = "REPOSITORY")
    private lateinit var rawSourcePath: String

    /**
     * The validated path to the source repository.
     *
     * This property represents the absolute path to the source repository that has been verified
     * to exist. It is initialized lazily and throws an [IllegalArgumentException] if the path does not exist.
     */
    protected val sourcePath: Path by lazy {
        Path.of(rawSourcePath).apply {
            require(exists()) { "Repository path $rawSourcePath not found" }
        }
    }

    /**
     * Executes the command, validates global flags, and runs the core functionality.
     *
     * This method sets the logging level based on debug/trace flags, configures log appenders,
     * validates global flags for compatibility with the current mode, and executes the [run] method.
     * If an exception occurs during execution, it logs the error (with or without stack trace
     * depending on the exception flag) and returns 1. On successful completion, returns 0.
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
            run()

            0
        } catch (e: Exception) {
            if (exception)
                logger.error(e.message?.failedStyle(), e)
            else
                logger.error(e.message?.failedStyle())
            1
        }
    }

    /**
     * Executes the core functionality of this command.
     *
     * This abstract method must be implemented by subclasses to define the specific behavior
     * when this command is executed. The implementation should contain the main logic for processing
     * the command, handling files, and performing operations based on the provided parameters.
     */
    protected abstract fun run()
}