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
 * Command to list the dependencies of a chart.
 *
 * Renders the KTS repository to Helm YAML and then executes `helm dependency list .` against the
 * rendered chart. The REPOSITORY (and optional TARGET) positionals are inherited from
 * [BaseRenderedHelmCommand].
 */
@Command(name = "list", aliases = ["ls"], description = ["List the dependencies of a chart with helm"])
class DependencyListCommand : BaseRenderedHelmCommand(), HelmArgsProvider {
    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--max-col-width"], description = ["$HELM_MARKER maximum column width for output table"], paramLabel = "UINT")
    private var maxColWidth: Int? = null

    override val helmCommand: List<String>
        get() = listOf("dependency", "list", ".")

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--max-col-width", maxColWidth)
    }
}
