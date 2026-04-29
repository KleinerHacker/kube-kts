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

sealed class BaseRootCommand : BaseCommand(), Callable<Int> {
    companion object {
        private val logger = logger()
    }

    @Parameters(index = "0", description = ["Path to the repository"], paramLabel = "REPOSITORY")
    private lateinit var rawSourcePath: String

    protected val sourcePath: Path by lazy {
        Path.of(rawSourcePath).apply {
            require(exists()) { "Repository path $rawSourcePath not found" }
        }
    }

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

    protected abstract fun run()
}