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
 * Command to search the Artifact Hub or your own hub instance for charts.
 *
 * Forwarded directly to `helm search hub [KEYWORD]`.
 */
@Command(name = "hub", description = ["Search for charts in the Artifact Hub or your own hub instance with helm"])
class SearchHubCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "KEYWORD", arity = "0..1", description = ["Keyword to search for (forwarded to helm as positional KEYWORD)"])
    private var keyword: String? = null

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--endpoint"], description = ["$HELM_MARKER Hub instance to query for charts"], paramLabel = "URL")
    private var endpoint: String? = null
    @Option(names = ["--fail-on-no-result"], description = ["$HELM_MARKER search fails if no results are found"])
    private var failOnNoResult: Boolean = false
    @Option(names = ["--list-repo-url"], description = ["$HELM_MARKER print charts repository URL"])
    private var listRepoUrl: Boolean = false
    @Option(names = ["--max-col-width"], description = ["$HELM_MARKER maximum column width for output table"], paramLabel = "UINT")
    private var maxColWidth: Int? = null
    @Option(names = ["-o", "--output"], description = ["$HELM_MARKER prints the output in the specified format. Allowed values: table, json, yaml"], paramLabel = "FORMAT")
    private var output: String? = null

    override val helmCommand: List<String>
        get() = buildList {
            add("search")
            add("hub")
            keyword?.let { add(it) }
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--endpoint", endpoint)
        flag("--fail-on-no-result", failOnNoResult)
        flag("--list-repo-url", listRepoUrl)
        opt("--max-col-width", maxColWidth)
        opt("--output", output)
    }
}
