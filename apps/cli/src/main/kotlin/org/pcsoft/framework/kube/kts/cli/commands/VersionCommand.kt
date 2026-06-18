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
 * Command to print the Helm version information.
 *
 * Does not operate on a release or chart: the call is forwarded directly to `helm version`.
 */
@Command(name = "version", description = ["Print the helm version information"])
class VersionCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--short"], description = ["$HELM_MARKER print the version number"])
    private var short: Boolean = false
    @Option(names = ["--template"], description = ["$HELM_MARKER template for version string format"], paramLabel = "TEMPLATE")
    private var template: String? = null

    override val helmCommand: List<String>
        get() = listOf("version")

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        flag("--short", short)
        opt("--template", template)
    }
}
