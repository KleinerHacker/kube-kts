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
 * Command for installing a KTS-based chart repository using Helm.
 *
 * This command renders the KTS repository to Helm YAML and then executes `helm install` against it,
 * forwarding all supported Helm install flags. The release name is passed via `--name` (forwarded to
 * Helm as the positional NAME); the `-n` shorthand is reserved for `--namespace` to stay in sync with Helm.
 */
@Command(name = "install", description = ["Install a KTS based chart repository with helm"])
class InstallCommand : BaseHelmCommand(), HelmArgsProvider {
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

    @Option(names = ["--atomic"], description = ["$HELM_MARKER if set, the installation process deletes the installation on failure"])
    private var atomic: Boolean = false
    @Option(names = ["--create-namespace"], description = ["$HELM_MARKER create the release namespace if not present"])
    private var createNamespace: Boolean = false
    @Option(names = ["--dry-run"], description = ["$HELM_MARKER simulate an install"])
    private var dryRun: Boolean = false
    @Option(names = ["--enable-dns"], description = ["$HELM_MARKER enable DNS lookups when rendering templates"])
    private var enableDns: Boolean = false
    @Option(names = ["--force"], description = ["$HELM_MARKER force resource updates through a replacement strategy"])
    private var force: Boolean = false
    @Option(names = ["-g", "--generate-name"], description = ["$HELM_MARKER generate the name (and omit the NAME parameter)"])
    private var generateName: Boolean = false
    @Option(names = ["-l", "--labels"], description = ["$HELM_MARKER labels that would be added to the release metadata (repeatable)"], paramLabel = "KEY=VALUE")
    private var labels: Array<String>? = null
    @Option(names = ["--description"], description = ["$HELM_MARKER add a custom description"], paramLabel = "TEXT")
    private var description: String? = null
    @Option(names = ["-o", "--output"], description = ["$HELM_MARKER prints the output in the specified format (table, json, yaml)"], paramLabel = "FORMAT")
    private var output: String? = null
    @Option(names = ["--replace"], description = ["$HELM_MARKER re-use the given name, only if that name is a deleted release which remains in the history"])
    private var replace: Boolean = false
    @Option(names = ["--wait"], description = ["$HELM_MARKER wait until all Pods, PVCs, Services and Deployments are in a ready state before marking the release as successful"])
    private var wait: Boolean = false
    @Option(names = ["--wait-for-jobs"], description = ["$HELM_MARKER if set and --wait enabled, will wait until all Jobs have been completed"])
    private var waitForJobs: Boolean = false

    override val helmCommand: List<String>
        get() = buildList {
            add("install")
            name?.let { add(it) }
            add(".")
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, valueOptions, chartSourceOptions, renderSharedOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        flag("--atomic", atomic)
        flag("--create-namespace", createNamespace)
        flag("--dry-run", dryRun)
        flag("--enable-dns", enableDns)
        flag("--force", force)
        flag("--generate-name", generateName)
        multi("--labels", labels)
        opt("--description", description)
        opt("--output", output)
        flag("--replace", replace)
        flag("--wait", wait)
        flag("--wait-for-jobs", waitForJobs)
    }
}
