package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.cli.GlobalFlags
import org.pcsoft.framework.kube.kts.cli.MainCommand
import org.pcsoft.framework.kube.kts.cli.types.YamlMergingAlgorithm
import org.pcsoft.framework.kube.kts.core.merge.YamlArrayMergeStrategy
import picocli.CommandLine.Mixin
import picocli.CommandLine.ParentCommand

/**
 * Abstract base class for command implementations, providing access to global flags and parent command.
 *
 * This sealed class serves as a foundation for various command types, offering protected properties that
 * consolidate flag values from both the current command's [globalFlags] and its parent command's flags.
 * It also includes validation logic for experimental features.
 */
sealed class BaseCommand {
    @ParentCommand
    private lateinit var parent: MainCommand
    @Mixin
    private lateinit var globalFlags: GlobalFlags

    /**
     * Flag indicating whether to throw exceptions instead of handling them gracefully.
     *
     * This property returns the value from the current command's global flags exception setting,
     * providing direct access without checking parent flags. Used for specific error handling scenarios.
     */
    protected val localException: Boolean
        get() = globalFlags.exception

    /**
     * Flag indicating whether debug mode is enabled.
     *
     * This property checks both the global debug flag and the parent's global debug flag,
     * returning true if either is set to true. Used for conditional logging and debugging output.
     */
    protected val isDebug: Boolean
        get() = globalFlags.debug || parent.globalFlags.debug

    /**
     * Indicates whether trace-level logging is enabled.
     *
     * This property checks the verbose flag in both the current command's global flags
     * and its parent command's global flags. If either flag is set to true, this property returns true,
     * enabling detailed trace-level logging throughout the application.
     */
    protected val isTrace: Boolean
        get() = globalFlags.verbose || parent.globalFlags.verbose

    /**
     * Determines whether detailed log levels should be shown.
     *
     * This property returns `true` if any of the following conditions are met:
     * - The global flag for showing log level is enabled
     * - The parent command's global flag for showing log level is enabled
     * - The debug mode is active
     * - The trace mode is active
     */
    protected val showLogLevel: Boolean
        get() = globalFlags.showLogLevel || parent.globalFlags.showLogLevel || isDebug || isTrace

    /**
     * Indicates whether exceptions should be thrown or logged.
     *
     * This property checks both the global exception flag and the parent's global exception flag.
     * If either is set to true, exceptions will be thrown; otherwise, they will only be logged.
     */
    protected val exception: Boolean
        get() = globalFlags.exception || parent.globalFlags.exception

    /**
     * Indicates whether experimental features are enabled.
     *
     * This property checks both the global flags and the parent's global flags to determine if experimental mode is active.
     * When true, experimental features may be available or behavior may differ from stable releases.
     */
    protected val experimentalMode: Boolean
        get() = globalFlags.experimentalMode || parent.globalFlags.experimentalMode

    /**
     * Indicates whether unsafe mode is enabled either globally or for the parent command.
     *
     * This property checks both the global flags and the parent's global flags to determine
     * if unsafe operations should be allowed. Unsafe mode typically bypasses certain safety
     * checks or restrictions in the application.
     */
    protected val unsafeMode: Boolean
        get() = globalFlags.unsafeMode || parent.globalFlags.unsafeMode

    /**
     * The algorithm used for merging YAML files.
     *
     * This property determines which YAML merging strategy will be applied when combining multiple YAML files.
     * It checks the global flags of the current instance and its parent (if any) to find a configured algorithm,
     * defaulting to [YamlMergingAlgorithm.HELM] if none is specified.
     */
    protected val yamlMergeAlgorithm: YamlMergingAlgorithm
        get() = globalFlags.yamlMergeAlgorithm ?: parent.globalFlags.yamlMergeAlgorithm ?: YamlMergingAlgorithm.HELM

    /**
     * Strategy used for merging YAML array nodes when processing multiple YAML structures.
     *
     * This property determines how elements of arrays in the base YAML document interact with elements
     * from overlay YAML documents during merge operations. Default is [YamlArrayMergeStrategy.Replace].
     */
    protected val yamlArrayMergeStrategy: YamlArrayMergeStrategy
        get() = globalFlags.yamlArrayMergeStrategy ?: parent.globalFlags.yamlArrayMergeStrategy ?: YamlArrayMergeStrategy.Replace

    /**
     * Validates global flags to ensure they are compatible with the current mode.
     *
     * Throws [IllegalStateException] if YAML merge algorithm or YAML array merge strategy is set
     * while not in experimental mode. These features are only supported in experimental mode.
     */
    protected fun validateGlobalFlags() {
        if (!experimentalMode) {
            if (globalFlags.yamlMergeAlgorithm != null || parent.globalFlags.yamlMergeAlgorithm != null)
                throw IllegalStateException("Yaml merge algorithm is not supported in non experimental mode")
            if (globalFlags.yamlArrayMergeStrategy != null || parent.globalFlags.yamlArrayMergeStrategy != null)
                throw IllegalStateException("Yaml array merge strategy is not supported in non experimental mode")
        }
    }
}