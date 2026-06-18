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
 * Command to update information of available charts locally from chart repositories.
 *
 * Forwarded directly to `helm repo update [REPO...]`. If no repository names are given, all
 * repositories are updated.
 */
@Command(name = "update", aliases = ["up"], description = ["Update information of available charts locally from chart repositories with helm"])
class RepoUpdateCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0..*", paramLabel = "REPO", arity = "0..*", description = ["Names of the repositories to update (forwarded to helm as positionals). If omitted, all repositories are updated."])
    private var repos: Array<String>? = null

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--fail-on-repo-update-fail"], description = ["$HELM_MARKER update fails if any of the repository updates fail"])
    private var failOnRepoUpdateFail: Boolean = false

    override val helmCommand: List<String>
        get() = buildList {
            add("repo")
            add("update")
            repos?.let { addAll(it) }
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        flag("--fail-on-repo-update-fail", failOnRepoUpdateFail)
    }
}
