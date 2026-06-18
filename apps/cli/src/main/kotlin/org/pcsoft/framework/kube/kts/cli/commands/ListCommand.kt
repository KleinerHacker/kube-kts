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

/**
 * Command to list Helm releases.
 *
 * Operates on the cluster, not on a chart, so it needs neither a repository nor a rendering step:
 * the call is forwarded directly to `helm list`.
 */
@Command(name = "list", aliases = ["ls"], description = ["List releases with helm"])
class ListCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["-a", "--all"], description = ["$HELM_MARKER show all releases without any filter applied"])
    private var all: Boolean = false
    @Option(names = ["-A", "--all-namespaces"], description = ["$HELM_MARKER list releases across all namespaces"])
    private var allNamespaces: Boolean = false
    @Option(names = ["-d", "--date"], description = ["$HELM_MARKER sort by release date"])
    private var date: Boolean = false
    @Option(names = ["--deployed"], description = ["$HELM_MARKER show deployed releases. If no other is specified, this will be automatically enabled"])
    private var deployed: Boolean = false
    @Option(names = ["--failed"], description = ["$HELM_MARKER show failed releases"])
    private var failed: Boolean = false
    @Option(names = ["--filter"], description = ["$HELM_MARKER a regular expression (Perl compatible). Any releases that match the expression will be included in the results"], paramLabel = "REGEXP")
    private var filter: String? = null
    @Option(names = ["-m", "--max"], description = ["$HELM_MARKER maximum number of releases to fetch"], paramLabel = "INT")
    private var max: Int? = null
    @Option(names = ["--no-headers"], description = ["$HELM_MARKER don't print headers when using the default output format"])
    private var noHeaders: Boolean = false
    @Option(names = ["--offset"], description = ["$HELM_MARKER next release index in the list, used to offset from start value"], paramLabel = "INT")
    private var offset: Int? = null
    @Option(names = ["-o", "--output"], description = ["$HELM_MARKER prints the output in the specified format. Allowed values: table, json, yaml"], paramLabel = "FORMAT")
    private var output: String? = null
    @Option(names = ["--pending"], description = ["$HELM_MARKER show pending releases"])
    private var pending: Boolean = false
    @Option(names = ["-r", "--reverse"], description = ["$HELM_MARKER reverse the sort order"])
    private var reverse: Boolean = false
    @Option(names = ["-l", "--selector"], description = ["$HELM_MARKER selector (label query) to filter on, supports '=', '==', and '!='"], paramLabel = "SELECTOR")
    private var selector: String? = null
    @Option(names = ["-q", "--short"], description = ["$HELM_MARKER output short (quiet) listing format"])
    private var short: Boolean = false
    @Option(names = ["--superseded"], description = ["$HELM_MARKER show superseded releases"])
    private var superseded: Boolean = false
    @Option(names = ["--time-format"], description = ["$HELM_MARKER format time using golang time formatter"], paramLabel = "FORMAT")
    private var timeFormat: String? = null
    @Option(names = ["--uninstalled"], description = ["$HELM_MARKER show uninstalled releases (if 'helm uninstall --keep-history' was used)"])
    private var uninstalled: Boolean = false
    @Option(names = ["--uninstalling"], description = ["$HELM_MARKER show releases that are currently being uninstalled"])
    private var uninstalling: Boolean = false

    override val helmCommand: List<String>
        get() = listOf("list")

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        flag("--all", all)
        flag("--all-namespaces", allNamespaces)
        flag("--date", date)
        flag("--deployed", deployed)
        flag("--failed", failed)
        opt("--filter", filter)
        opt("--max", max)
        flag("--no-headers", noHeaders)
        opt("--offset", offset)
        opt("--output", output)
        flag("--pending", pending)
        flag("--reverse", reverse)
        opt("--selector", selector)
        flag("--short", short)
        flag("--superseded", superseded)
        opt("--time-format", timeFormat)
        flag("--uninstalled", uninstalled)
        flag("--uninstalling", uninstalling)
    }
}
