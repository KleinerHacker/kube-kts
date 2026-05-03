package org.pcsoft.framework.kube.kts.cli

import org.pcsoft.framework.kube.kts.cli.intern.NoArgs
import org.pcsoft.framework.kube.kts.cli.types.YamlMergingAlgorithm
import org.pcsoft.framework.kube.kts.core.merge.YamlArrayMergeStrategy
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Option

@NoArgs
class GlobalFlags(
    @field:ArgGroup(heading = "@|bold Global Flags|@%n", exclusive = false, multiplicity = "0..1")
    val default: DefaultFlagsImpl = DefaultFlagsImpl(),
    @field:ArgGroup(heading = "@|italic Experimental Flags|@%n", exclusive = false, multiplicity = "0..1")
    val experimental: ExperimentalFlagsImpl = ExperimentalFlagsImpl()
) : DefaultFlags by default, ExperimentalFlags by experimental

/**
 * Interface defining default global flags for the CLI application.
 *
 * These flags control basic runtime behavior such as logging verbosity,
 * error handling, and safety features across all commands.
 */
interface DefaultFlags {
    /**
     * Flag indicating whether debug mode is enabled.
     *
     * When set to true, enables additional diagnostic output and detailed logging across all CLI operations.
     * Debug mode provides more verbose information about the execution process, which can be useful for troubleshooting
     * or understanding the internal behavior of commands. This flag affects both the application's own logging and
     * potentially the underlying Helm operations when applicable.
     */
    val debug: Boolean

    /**
     * Flag indicating whether verbose mode is enabled.
     *
     * When set to true, enables additional output and detailed logging across all CLI operations.
     * Verbose mode provides more comprehensive information about the execution process, which can be useful for troubleshooting
     * or understanding the internal behavior of commands. This flag affects both the application's own logging and
     * potentially the underlying Helm operations when applicable.
     */
    val verbose: Boolean

    /**
     * Flag indicating whether log level information should be displayed.
     *
     * When set to true, the CLI will output additional information about the current logging verbosity,
     * including which log level is active (e.g., debug, verbose, or standard). This helps users understand
     * the current diagnostic output level and can be useful for troubleshooting or verifying configuration.
     */
    val showLogLevel: Boolean

    /**
     * Flag indicating whether exception handling should be enabled.
     *
     * When set to true, the CLI will propagate exceptions to the caller rather than catching and displaying them.
     * This allows for more detailed error reporting and debugging in external systems or scripts that consume this CLI.
     * In normal operation (when false), exceptions are caught and displayed as user-friendly error messages.
     */
    val exception: Boolean

    /**
     * Flag indicating whether experimental features are enabled.
     *
     * When set to true, this enables access to experimental functionality and configuration options that may be unstable or subject to change.
     * Experimental mode allows the use of advanced YAML merging algorithms and array merge strategies which are otherwise restricted in standard mode.
     */
    val experimentalMode: Boolean

    /**
     * Flag indicating whether unsafe mode is enabled.
     *
     * When set to true, this flag allows operations that may bypass certain safety checks or restrictions,
     * potentially leading to unstable or unpredictable behavior. Use with caution as it can affect the stability
     * and security of the application.
     */
    val unsafeMode: Boolean
}

/**
 * Implementation of [DefaultFlags] providing command-line options for controlling basic runtime behavior.
 *
 * This class defines flags that control logging verbosity, error handling, and safety features across all CLI operations.
 * Each flag is configurable via command-line arguments with corresponding descriptions for help output.
 */
@NoArgs
class DefaultFlagsImpl(
    @field:Option(names = ["--debug"], description = ["Print debug information with log level"])
    override var debug: Boolean = false,
    @field:Option(names = ["--verbose"], description = ["Print all information with log level"])
    override var verbose: Boolean = false,
    @field:Option(names = ["--show-log-level"], description = ["Print log level of information output"])
    override var showLogLevel: Boolean = false,
    @field:Option(names = ["--exception"], description = ["Print exceptions in case of errors"])
    override var exception: Boolean = false,
    @field:Option(
        names = ["--experimental"],
        description = ["Enable experimental features, see @|italic Experimental Flags|@ section"]
    )
    override var experimentalMode: Boolean = false,
    @field:Option(
        names = ["--unsafe"],
        description = [
            "Enable unsafe mode, allowing certain operations that may be @|italic dangerous|@",
            "\t@|italic Allow imports in scripts|@"
        ]
    )
    override var unsafeMode: Boolean = false,
) : DefaultFlags

/**
 * Interface defining experimental flags for customizing YAML merging behavior.
 *
 * This interface provides configuration options for controlling how YAML files and arrays are merged during processing.
 * The flags allow selection of different merging algorithms and array merge strategies, enabling advanced use cases
 * that may differ from standard Helm behavior. These features are considered experimental and may change in future versions.
 */
interface ExperimentalFlags {
    /**
     * Specifies the algorithm to use for merging YAML files during processing.
     *
     * This property determines which YAML merging strategy will be applied when combining multiple YAML sources.
     * When set to [YamlMergingAlgorithm.INTERNAL], an internal merging implementation is used, allowing customization
     * of array merge behavior through [yamlArrayMergeStrategy]. When set to [YamlMergingAlgorithm.HELM], the standard
     * Helm YAML merging algorithm is applied instead. If null, a default value will be determined based on other configuration.
     *
     * @see YamlMergingAlgorithm
     */
    val yamlMergeAlgorithm: YamlMergingAlgorithm?

    /**
     * Specifies the strategy for merging YAML arrays during processing.
     *
     * This property determines how array elements from overlay YAML documents should be merged with
     * existing array elements in base YAML documents. The strategy controls whether new elements are
     * added to the beginning, end, or replace the entire array, or if no merging should occur at all.
     * When null, a default value of [YamlArrayMergeStrategy.Replace] will be used unless overridden by other configuration.
     *
     * @see YamlArrayMergeStrategy
     */
    val yamlArrayMergeStrategy: YamlArrayMergeStrategy?
}

/**
 * Implementation of experimental flags for customizing YAML merging behavior.
 *
 * This class provides configuration options for controlling how YAML files and arrays are merged during processing,
 * allowing selection of different merging algorithms and array merge strategies. These features are considered experimental
 * and may change in future versions.
 */
@NoArgs
class ExperimentalFlagsImpl(
    @field:Option(
        names = ["--yaml-merge"],
        description = [
            "Algorithm to use for merging yaml files (@|italic requires experimental mode|@)",
            "\t@|cyan HELM|@ to use merging algorithm of Helm (@|bold default|@)",
            "\t@|cyan INTERNAL|@ to use internal custom algorithm (@|italic experimental|@)"
        ],
        paramLabel = "TYPE"
    )
    override var yamlMergeAlgorithm: YamlMergingAlgorithm? = null,
    @field:Option(
        names = ["--yaml-array-merge"],
        description = [
            "Strategy to use for merging YAML arrays (@|italic requires experimental mode|@)",
            "\t@|cyan None|@ Do not change array in base YAML",
            "\t@|cyan Replace|@ Replace array in base YAML with overlay array (@|bold default|@)",
            "\t@|cyan AddFirst|@ Add overlay array to beginning of base array",
            "\t@|cyan AddLast|@ Add overlay array to end of base array"
        ],
        paramLabel = "TYPE"
    )
    override var yamlArrayMergeStrategy: YamlArrayMergeStrategy? = null,
) : ExperimentalFlags