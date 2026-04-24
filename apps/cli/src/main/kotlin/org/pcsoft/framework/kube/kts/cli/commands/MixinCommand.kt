package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.cli.GlobalFlags
import org.pcsoft.framework.kube.kts.cli.MainCommand
import picocli.CommandLine.Mixin
import picocli.CommandLine.ParentCommand

sealed class MixinCommand {
    @ParentCommand
    private lateinit var parent: MainCommand
    @Mixin
    private lateinit var globalFlags: GlobalFlags

    protected val isDebug: Boolean
        get() = globalFlags.debug || parent.globalFlags.debug

    protected val isTrace: Boolean
        get() = globalFlags.verbose || parent.globalFlags.verbose

    protected val showLogLevel: Boolean
        get() = globalFlags.showLogLevel || parent.globalFlags.showLogLevel || isDebug || isTrace

    protected val exception: Boolean
        get() = globalFlags.exception || parent.globalFlags.exception
}