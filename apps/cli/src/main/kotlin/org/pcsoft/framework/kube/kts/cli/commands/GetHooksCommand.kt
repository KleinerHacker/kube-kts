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
 * Command to download all hooks for a named release.
 *
 * Operates on an already installed release; forwarded directly to `helm get hooks <RELEASE>`.
 */
@Command(name = "hooks", description = ["Download all hooks for a named release with helm"])
class GetHooksCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "RELEASE", description = ["Name of the release to query (forwarded to helm as positional RELEASE)"])
    private lateinit var release: String

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--revision"], description = ["$HELM_MARKER get the named release with revision"], paramLabel = "INT")
    private var revision: Int? = null

    override val helmCommand: List<String>
        get() = listOf("get", "hooks", release)

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--revision", revision)
    }
}
