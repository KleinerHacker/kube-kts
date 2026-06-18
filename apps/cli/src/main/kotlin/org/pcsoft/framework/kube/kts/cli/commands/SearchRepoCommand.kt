/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.cli.commands

import org.pcsoft.framework.kube.kts.cli.commands.helm.HelmArgsProvider
import org.pcsoft.framework.kube.kts.cli.commands.helm.HelmGlobalOptions
import org.pcsoft.framework.kube.kts.cli.intern.utils.HELM_MARKER
import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

/**
 * Command to search repositories for a keyword in charts.
 *
 * Forwarded directly to `helm search repo [KEYWORD]`.
 */
@Command(name = "repo", description = ["Search repositories for a keyword in charts with helm"])
class SearchRepoCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "KEYWORD", arity = "0..1", description = ["Keyword to search for (forwarded to helm as positional KEYWORD)"])
    private var keyword: String? = null

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--devel"], description = ["$HELM_MARKER use development versions (alpha, beta, and release candidate releases), too. Equivalent to version '>0.0.0-0'"])
    private var devel: Boolean = false
    @Option(names = ["--fail-on-no-result"], description = ["$HELM_MARKER search fails if no results are found"])
    private var failOnNoResult: Boolean = false
    @Option(names = ["--max-col-width"], description = ["$HELM_MARKER maximum column width for output table"], paramLabel = "UINT")
    private var maxColWidth: Int? = null
    @Option(names = ["-o", "--output"], description = ["$HELM_MARKER prints the output in the specified format. Allowed values: table, json, yaml"], paramLabel = "FORMAT")
    private var output: String? = null
    @Option(names = ["-r", "--regexp"], description = ["$HELM_MARKER use regular expressions for searching repositories you have added"])
    private var regexp: Boolean = false
    @Option(names = ["--version"], description = ["$HELM_MARKER search using semantic versioning constraints on repositories you have added"], paramLabel = "VERSION")
    private var version: String? = null
    @Option(names = ["-l", "--versions"], description = ["$HELM_MARKER show the long listing, with each version of each chart on its own line, for repositories you have added"])
    private var versions: Boolean = false

    override val helmCommand: List<String>
        get() = buildList {
            add("search")
            add("repo")
            keyword?.let { add(it) }
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        flag("--devel", devel)
        flag("--fail-on-no-result", failOnNoResult)
        opt("--max-col-width", maxColWidth)
        opt("--output", output)
        flag("--regexp", regexp)
        opt("--version", version)
        flag("--versions", versions)
    }
}
