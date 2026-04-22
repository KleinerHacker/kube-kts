package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.cli.MainCommand
import org.pcsoft.framework.kube.kts.cli.intern.utils.logger
import org.pcsoft.framework.kube.kts.cli.intern.utils.red
import picocli.CommandLine.ParentCommand
import java.util.concurrent.Callable

sealed class BaseCommand : Callable<Int> {
    companion object {
        private val logger = logger()
    }

    @ParentCommand
    protected lateinit var parent: MainCommand private set

    final override fun call(): Int = try {
        run()
        0
    } catch (e: Exception) {
        if (parent.exception)
            logger.error(e.message?.red(), e)
        else
            logger.error(e.message?.red())
        1
    }

    protected abstract fun run()
}