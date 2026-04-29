package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.cli.GlobalFlags
import org.pcsoft.framework.kube.kts.cli.MainCommand
import org.pcsoft.framework.kube.kts.cli.types.YamlMergingAlgorithm
import org.pcsoft.framework.kube.kts.core.merge.YamlArrayMergeStrategy
import picocli.CommandLine.Mixin
import picocli.CommandLine.ParentCommand

sealed class BaseCommand {
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

    protected val experimentalMode: Boolean
        get() = globalFlags.experimentalMode || parent.globalFlags.experimentalMode

    protected val unsafeMode: Boolean
        get() = globalFlags.unsafeMode || parent.globalFlags.unsafeMode

    protected val yamlMergeAlgorithm: YamlMergingAlgorithm
        get() = globalFlags.yamlMergeAlgorithm ?: parent.globalFlags.yamlMergeAlgorithm ?: YamlMergingAlgorithm.HELM

    protected val yamlArrayMergeStrategy: YamlArrayMergeStrategy
        get() = globalFlags.yamlArrayMergeStrategy ?: parent.globalFlags.yamlArrayMergeStrategy ?: YamlArrayMergeStrategy.Replace

    protected fun validateGlobalFlags() {
        if (!experimentalMode) {
            if (globalFlags.yamlMergeAlgorithm != null || parent.globalFlags.yamlMergeAlgorithm != null)
                throw IllegalStateException("Yaml merge algorithm is not supported in non experimental mode")
            if (globalFlags.yamlArrayMergeStrategy != null || parent.globalFlags.yamlArrayMergeStrategy != null)
                throw IllegalStateException("Yaml array merge strategy is not supported in non experimental mode")
        }
    }
}