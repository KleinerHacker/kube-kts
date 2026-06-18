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
 * Command to update the "charts/" directory based on the contents of "Chart.yaml".
 *
 * Renders the KTS repository to Helm YAML and then executes `helm dependency update .` against the
 * rendered chart. The REPOSITORY (and optional TARGET) positionals are inherited from
 * [BaseRenderedHelmCommand].
 */
@Command(name = "update", aliases = ["up"], description = ["Update charts/ based on the contents of Chart.yaml with helm"])
class DependencyUpdateCommand : BaseRenderedHelmCommand(), HelmArgsProvider {
    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--keyring"], description = ["$HELM_MARKER keyring containing public keys"], paramLabel = "FILE")
    private var keyring: String? = null
    @Option(names = ["--skip-refresh"], description = ["$HELM_MARKER do not refresh the local repository cache"])
    private var skipRefresh: Boolean = false
    @Option(names = ["--verify"], description = ["$HELM_MARKER verify the packages against signatures"])
    private var verify: Boolean = false

    override val helmCommand: List<String>
        get() = listOf("dependency", "update", ".")

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--keyring", keyring)
        flag("--skip-refresh", skipRefresh)
        flag("--verify", verify)
    }
}
