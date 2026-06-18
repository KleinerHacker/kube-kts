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
import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Parameters

/**
 * Command to remove one or more chart repositories.
 *
 * Forwarded directly to `helm repo remove <REPO...>`.
 */
@Command(name = "remove", aliases = ["rm"], description = ["Remove one or more chart repositories with helm"])
class RepoRemoveCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0..*", paramLabel = "REPO", arity = "1..*", description = ["Names of the repositories to remove (forwarded to helm as positionals)"])
    private lateinit var repos: Array<String>

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    override val helmCommand: List<String>
        get() = buildList {
            add("repo")
            add("remove")
            addAll(repos)
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = emptyList()
}
