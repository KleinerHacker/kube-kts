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

import org.pcsoft.framework.kube.kts.cli.commands.helm.*
import org.pcsoft.framework.kube.kts.cli.intern.utils.HELM_MARKER
import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option

/**
 * Command for upgrading a KTS-based chart repository using Helm.
 *
 * This command renders the KTS repository to Helm YAML and then executes `helm upgrade` against it,
 * forwarding all supported Helm upgrade flags. The release name is passed via `--name` (forwarded to
 * Helm as the positional NAME); the `-n` shorthand is reserved for `--namespace` to stay in sync with Helm.
 */
@Command(name = "upgrade", description = ["Upgrade a KTS based chart repository with helm"])
class UpgradeCommand : BaseHelmCommand(), HelmArgsProvider {
    @Option(names = ["--name"], description = ["Name of the release (forwarded to helm as positional NAME)"], paramLabel = "NAME")
    private var name: String? = null

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions
    @Mixin
    private lateinit var valueOptions: HelmValueOptions
    @Mixin
    private lateinit var chartSourceOptions: HelmChartSourceOptions
    @Mixin
    private lateinit var renderSharedOptions: HelmRenderSharedOptions

    @Option(names = ["-i", "--install"], description = ["$HELM_MARKER if a release by this name doesn't already exist, run an install"])
    private var install: Boolean = false
    @Option(names = ["--atomic"], description = ["$HELM_MARKER if set, upgrade process rolls back changes made in case of failed upgrade"])
    private var atomic: Boolean = false
    @Option(names = ["--cleanup-on-fail"], description = ["$HELM_MARKER allow deletion of new resources created in this upgrade when upgrade fails"])
    private var cleanupOnFail: Boolean = false
    @Option(names = ["--create-namespace"], description = ["$HELM_MARKER if --install is set, create the release namespace if not present"])
    private var createNamespace: Boolean = false
    @Option(names = ["--dry-run"], description = ["$HELM_MARKER simulate an upgrade"])
    private var dryRun: Boolean = false
    @Option(names = ["--enable-dns"], description = ["$HELM_MARKER enable DNS lookups when rendering templates"])
    private var enableDns: Boolean = false
    @Option(names = ["--force"], description = ["$HELM_MARKER force resource updates through a replacement strategy"])
    private var force: Boolean = false
    @Option(names = ["--history-max"], description = ["$HELM_MARKER limit the maximum number of revisions saved per release (0 for no limit)"], paramLabel = "INT")
    private var historyMax: Int? = null
    @Option(names = ["-l", "--labels"], description = ["$HELM_MARKER labels that would be added to the release metadata (repeatable)"], paramLabel = "KEY=VALUE")
    private var labels: Array<String>? = null
    @Option(names = ["--description"], description = ["$HELM_MARKER add a custom description"], paramLabel = "TEXT")
    private var description: String? = null
    @Option(names = ["-o", "--output"], description = ["$HELM_MARKER prints the output in the specified format (table, json, yaml)"], paramLabel = "FORMAT")
    private var output: String? = null
    @Option(names = ["--reset-values"], description = ["$HELM_MARKER when upgrading, reset the values to the ones built into the chart"])
    private var resetValues: Boolean = false
    @Option(names = ["--reuse-values"], description = ["$HELM_MARKER when upgrading, reuse the last release's values and merge in any overrides"])
    private var reuseValues: Boolean = false
    @Option(names = ["--reset-then-reuse-values"], description = ["$HELM_MARKER when upgrading, reset the values to the ones built into the chart, apply the last release's values and merge in any overrides"])
    private var resetThenReuseValues: Boolean = false
    @Option(names = ["--take-ownership"], description = ["$HELM_MARKER if set, upgrade will ignore the check for helm annotations and take ownership of existing resources"])
    private var takeOwnership: Boolean = false
    @Option(names = ["--wait"], description = ["$HELM_MARKER wait until all Pods, PVCs, Services and Deployments are in a ready state before marking the release as successful"])
    private var wait: Boolean = false
    @Option(names = ["--wait-for-jobs"], description = ["$HELM_MARKER if set and --wait enabled, will wait until all Jobs have been completed"])
    private var waitForJobs: Boolean = false

    override val helmCommand: List<String>
        get() = buildList {
            add("upgrade")
            name?.let { add(it) }
            add(".")
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, valueOptions, chartSourceOptions, renderSharedOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        flag("--install", install)
        flag("--atomic", atomic)
        flag("--cleanup-on-fail", cleanupOnFail)
        flag("--create-namespace", createNamespace)
        flag("--dry-run", dryRun)
        flag("--enable-dns", enableDns)
        flag("--force", force)
        opt("--history-max", historyMax)
        multi("--labels", labels)
        opt("--description", description)
        opt("--output", output)
        flag("--reset-values", resetValues)
        flag("--reuse-values", reuseValues)
        flag("--reset-then-reuse-values", resetThenReuseValues)
        flag("--take-ownership", takeOwnership)
        flag("--wait", wait)
        flag("--wait-for-jobs", waitForJobs)
    }
}
