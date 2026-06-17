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
 * Command to show the status of a named Helm release.
 *
 * Operates on an already installed release and therefore needs **neither** a KTS/YAML repository
 * **nor** a rendering step: the call is forwarded directly to `helm status <RELEASE>`. For that
 * reason it derives from [BaseDirectHelmCommand] (no Scan/Compile/Render) instead of
 * [BaseRenderedHelmCommand].
 */
@Command(name = "status", description = ["Show the status of a named release with helm"])
class StatusCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "RELEASE", description = ["Name of the release to query (forwarded to helm as positional RELEASE)"])
    private lateinit var release: String

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--revision"], description = ["$HELM_MARKER if set, display the status of the named release with revision"], paramLabel = "INT")
    private var revision: Int? = null
    @Option(names = ["--output"], description = ["$HELM_MARKER prints the output in the specified format. Allowed values: table, json, yaml"], paramLabel = "FORMAT")
    private var output: String? = null
    @Option(names = ["--show-desc"], description = ["$HELM_MARKER if set, display the description message of the named release"])
    private var showDesc: Boolean = false
    @Option(names = ["--show-resources"], description = ["$HELM_MARKER if set, display the resources of the named release"])
    private var showResources: Boolean = false

    override val helmCommand: List<String>
        get() = listOf("status", release)

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--revision", revision)
        opt("--output", output)
        flag("--show-desc", showDesc)
        flag("--show-resources", showResources)
    }
}
