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
import org.pcsoft.framework.kube.kts.cli.commands.helm.HelmValueOptions
import org.pcsoft.framework.kube.kts.cli.intern.utils.HELM_MARKER
import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option

/**
 * Command to preview the changes an `upgrade` would apply, using the external `helm-diff` plugin.
 *
 * Renders the KTS repository to Helm YAML and then executes `helm diff upgrade <RELEASE> .` against
 * the rendered chart. The release name is passed via `--name` (forwarded to the plugin as the
 * positional RELEASE), since the REPOSITORY/TARGET positionals are inherited from
 * [BaseRenderedHelmCommand].
 *
 * Note: requires the external `helm-diff` plugin to be installed.
 */
@Command(name = "upgrade", description = ["Preview the changes an upgrade would apply with the helm-diff plugin"])
class DiffUpgradeCommand : BaseRenderedHelmCommand(), HelmArgsProvider {
    @Option(names = ["--name"], description = ["Name of the release (forwarded to helm-diff as positional RELEASE)"], paramLabel = "NAME")
    private var name: String? = null

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions
    @Mixin
    private lateinit var valueOptions: HelmValueOptions

    @Option(names = ["--detailed-exitcode"], description = ["$HELM_MARKER return a non-zero exit code (2) when there are changes"])
    private var detailedExitcode: Boolean = false
    @Option(names = ["--context"], description = ["$HELM_MARKER output NUM lines of context around changes (-1 for full context)"], paramLabel = "NUM")
    private var context: Int? = null
    @Option(names = ["--show-secrets"], description = ["$HELM_MARKER do not redact secret values in the output"])
    private var showSecrets: Boolean = false
    @Option(names = ["--no-hooks"], description = ["$HELM_MARKER disable diffing of hooks"])
    private var noHooks: Boolean = false
    @Option(names = ["--include-tests"], description = ["$HELM_MARKER enable the diffing of the helm test hooks"])
    private var includeTests: Boolean = false
    @Option(names = ["--reset-values"], description = ["$HELM_MARKER reset the values to the ones built into the chart and merge in any new values"])
    private var resetValues: Boolean = false
    @Option(names = ["--reuse-values"], description = ["$HELM_MARKER reuse the last release's values and merge in any new values"])
    private var reuseValues: Boolean = false
    @Option(names = ["--normalize-manifests"], description = ["$HELM_MARKER normalize manifests before running diff to exclude style differences from the output"])
    private var normalizeManifests: Boolean = false

    override val helmCommand: List<String>
        get() = buildList {
            add("diff")
            add("upgrade")
            name?.let { add(it) }
            add(".")
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, valueOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        flag("--detailed-exitcode", detailedExitcode)
        opt("--context", context)
        flag("--show-secrets", showSecrets)
        flag("--no-hooks", noHooks)
        flag("--include-tests", includeTests)
        flag("--reset-values", resetValues)
        flag("--reuse-values", reuseValues)
        flag("--normalize-manifests", normalizeManifests)
    }
}
