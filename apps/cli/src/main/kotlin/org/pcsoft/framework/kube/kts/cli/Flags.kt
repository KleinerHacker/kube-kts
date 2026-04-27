package org.pcsoft.framework.kube.kts.cli

import org.pcsoft.framework.kube.kts.cli.intern.NoArgs
import org.pcsoft.framework.kube.kts.cli.types.YamlMergingType
import org.pcsoft.framework.kube.kts.logging.symbolBullet
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
    @field:Option(names = ["--experimental"], description = ["Enable experimental features, see @|italic Experimental Flags|@ section"])
    override var experimentalMode: Boolean = false,
) : DefaultFlags

interface ExperimentalFlags {
    val yamlMergeAlgorithm: YamlMergingType?
}

@NoArgs
private class ExperimentalFlagsImpl(
    @field:Option(
        names = ["--yaml-merge"],
        description = [
            "Algorithm to use for merging yaml files",
            "$symbolBullet @|cyan HELM|@ to use merging algorithm of Helm (@|bold default|@)",
            "$symbolBullet @|cyan INTERNAL|@ to use internal custom algorithm (@|italic experimental|@)"
        ],
        paramLabel = "TYPE"
    )
    override var yamlMergeAlgorithm: YamlMergingType? = null
) : ExperimentalFlags