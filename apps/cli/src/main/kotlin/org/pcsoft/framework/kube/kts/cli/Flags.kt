package org.pcsoft.framework.kube.kts.cli

import org.pcsoft.framework.kube.kts.cli.intern.NoArgs
import org.pcsoft.framework.kube.kts.cli.types.YamlMergingAlgorithm
import org.pcsoft.framework.kube.kts.core.merge.YamlArrayMergeStrategy
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Option

@NoArgs
class GlobalFlags(
    @field:ArgGroup(heading = "@|bold Global Flags|@%n", exclusive = false, multiplicity = "0..1")
    val default: DefaultFlags = DefaultFlagsImpl(),
    @field:ArgGroup(heading = "@|italic Experimental Flags|@%n", exclusive = false, multiplicity = "0..1")
    val experimental: ExperimentalFlags = ExperimentalFlagsImpl()
) : DefaultFlags by default, ExperimentalFlags by experimental

interface DefaultFlags {
    val debug: Boolean
    val verbose: Boolean
    val showLogLevel: Boolean
    val exception: Boolean
    val experimentalMode: Boolean
    val unsafeMode: Boolean
}

@NoArgs
private class DefaultFlagsImpl(
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

interface ExperimentalFlags {
    val yamlMergeAlgorithm: YamlMergingAlgorithm?
    val yamlArrayMergeStrategy: YamlArrayMergeStrategy?
}

@NoArgs
private class ExperimentalFlagsImpl(
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
            "\t@|cyan Merge|@ Merge arrays in base YAML with overlay array (@|italic experimental|@)",
            "\t@|cyan AddFirst|@ Add overlay array to beginning of base array",
            "\t@|cyan AddLast|@ Add overlay array to end of base array"
        ],
        paramLabel = "TYPE"
    )
    override var yamlArrayMergeStrategy: YamlArrayMergeStrategy? = null,
) : ExperimentalFlags