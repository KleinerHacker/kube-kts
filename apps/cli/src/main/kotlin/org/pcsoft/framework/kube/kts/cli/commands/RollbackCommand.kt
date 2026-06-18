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
 * Command to roll a release back to a previous revision.
 *
 * Operates on an already installed release; forwarded directly to `helm rollback <RELEASE> [REVISION]`.
 * If no revision is given, Helm rolls back to the previous one.
 */
@Command(name = "rollback", description = ["Roll back a release to a previous revision with helm"])
class RollbackCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "RELEASE", description = ["Name of the release to roll back (forwarded to helm as positional RELEASE)"])
    private lateinit var release: String
    @Parameters(index = "1", paramLabel = "REVISION", arity = "0..1", description = ["Revision to roll back to. If omitted, Helm rolls back to the previous revision."])
    private var revision: String? = null

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--cleanup-on-fail"], description = ["$HELM_MARKER allow deletion of new resources created in this rollback when rollback fails"])
    private var cleanupOnFail: Boolean = false
    @Option(names = ["--dry-run"], description = ["$HELM_MARKER simulate a rollback"])
    private var dryRun: Boolean = false
    @Option(names = ["--force"], description = ["$HELM_MARKER force resource update through delete/recreate if needed"])
    private var force: Boolean = false
    @Option(names = ["--history-max"], description = ["$HELM_MARKER limit the maximum number of revisions saved per release. Use 0 for no limit"], paramLabel = "INT")
    private var historyMax: Int? = null
    @Option(names = ["--no-hooks"], description = ["$HELM_MARKER prevent hooks from running during rollback"])
    private var noHooks: Boolean = false
    @Option(names = ["--recreate-pods"], description = ["$HELM_MARKER performs pods restart for the resource if applicable"])
    private var recreatePods: Boolean = false
    @Option(names = ["--timeout"], description = ["$HELM_MARKER time to wait for any individual Kubernetes operation"], paramLabel = "DURATION")
    private var timeout: String? = null
    @Option(names = ["--wait"], description = ["$HELM_MARKER if set, will wait until all resources are in a ready state before marking the release as successful"])
    private var wait: Boolean = false
    @Option(names = ["--wait-for-jobs"], description = ["$HELM_MARKER if set and --wait enabled, will wait until all Jobs have been completed before marking the release as successful"])
    private var waitForJobs: Boolean = false

    override val helmCommand: List<String>
        get() = buildList {
            add("rollback")
            add(release)
            revision?.let { add(it) }
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        flag("--cleanup-on-fail", cleanupOnFail)
        flag("--dry-run", dryRun)
        flag("--force", force)
        opt("--history-max", historyMax)
        flag("--no-hooks", noHooks)
        flag("--recreate-pods", recreatePods)
        opt("--timeout", timeout)
        flag("--wait", wait)
        flag("--wait-for-jobs", waitForJobs)
    }
}
