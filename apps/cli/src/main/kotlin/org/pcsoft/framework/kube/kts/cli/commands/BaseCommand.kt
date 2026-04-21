package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.cli.MainCommand
import picocli.CommandLine.ParentCommand
import java.util.concurrent.Callable

sealed class BaseCommand : Callable<Int> {
    @ParentCommand
    protected lateinit var parent: MainCommand private set

    final override fun call(): Int = try {
        run()
        0
    } catch (e: Exception) {
        if (parent.exception)
            throw e

        System.err.println(e.message)
        1
    }

    protected abstract fun run()
}