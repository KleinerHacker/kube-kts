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
 * Command to uninstall a KTS-based chart repository using Helm.
 *
 * Executes `helm uninstall` for one or more release names, forwarding all supported Helm uninstall flags.
 * The release names are passed via `--name` (forwarded to Helm as positional RELEASE arguments).
 */
@Command(name = "uninstall", description = ["Uninstall a KTS based chart repository with helm"])
class UninstallCommand : BaseHelmCommand(), HelmArgsProvider {
    @Option(names = ["--name"], required = true, description = ["Name of the release(s) to uninstall (repeatable, forwarded to helm as positional RELEASE)"], paramLabel = "RELEASE")
    private lateinit var releases: Array<String>

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--cascade"], description = ["$HELM_MARKER must be \"background\", \"orphan\", or \"foreground\""], paramLabel = "STRING")
    private var cascade: String? = null
    @Option(names = ["--description"], description = ["$HELM_MARKER add a custom description"], paramLabel = "TEXT")
    private var description: String? = null
    @Option(names = ["--dry-run"], description = ["$HELM_MARKER simulate a uninstall"])
    private var dryRun: Boolean = false
    @Option(names = ["--ignore-not-found"], description = ["$HELM_MARKER treat \"release not found\" as a successful uninstall"])
    private var ignoreNotFound: Boolean = false
    @Option(names = ["--keep-history"], description = ["$HELM_MARKER remove all associated resources and mark the release as deleted, but retain the release history"])
    private var keepHistory: Boolean = false
    @Option(names = ["--no-hooks"], description = ["$HELM_MARKER prevent hooks from running during uninstallation"])
    private var noHooks: Boolean = false
    @Option(names = ["--timeout"], description = ["$HELM_MARKER time to wait for any individual Kubernetes operation"], paramLabel = "DURATION")
    private var timeout: String? = null
    @Option(names = ["--wait"], description = ["$HELM_MARKER if set, will wait until all the resources are deleted before returning"])
    private var wait: Boolean = false

    override val helmCommand: List<String>
        get() = buildList {
            add("uninstall")
            addAll(releases)
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--cascade", cascade)
        opt("--description", description)
        flag("--dry-run", dryRun)
        flag("--ignore-not-found", ignoreNotFound)
        flag("--keep-history", keepHistory)
        flag("--no-hooks", noHooks)
        opt("--timeout", timeout)
        flag("--wait", wait)
    }
}
