package org.pcsoft.framework.kube.kts.cli.commands

import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.pcsoft.framework.kube.kts.logging.failedStyle
import org.pcsoft.framework.kube.kts.logging.logger
import java.util.concurrent.Callable

sealed class BaseCommand : MixinCommand(), Callable<Int> {
    companion object {
        private val logger = logger()
    }

    final override fun call(): Int {
        val level = if (isTrace) Level.TRACE else if (isDebug) Level.DEBUG else Level.INFO
        LogManager.getRootLogger().level = level

        return try {
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