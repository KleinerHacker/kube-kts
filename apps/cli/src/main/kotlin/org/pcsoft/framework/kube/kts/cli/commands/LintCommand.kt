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
 * Command for linting a KTS-based Helm chart repository.
 *
 * Renders the KTS repository to Helm YAML and executes `helm lint` against it, forwarding all
 * supported Helm lint flags.
 */
@Command(name = "lint", description = ["Linting a KTS based chart repository with helm"])
class LintCommand : BaseHelmCommand(), HelmArgsProvider {
    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions
    @Mixin
    private lateinit var valueOptions: HelmValueOptions

    @Option(names = ["--quiet"], description = ["$HELM_MARKER print only warnings and errors"])
    private var quiet: Boolean = false
    @Option(names = ["--strict"], description = ["$HELM_MARKER fail on lint warnings"])
    private var strict: Boolean = false
    @Option(names = ["--with-subcharts"], description = ["$HELM_MARKER lint dependent charts"])
    private var withSubcharts: Boolean = false

    override val helmCommand: List<String>
        get() = listOf("lint", ".")

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, valueOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        flag("--quiet", quiet)
        flag("--strict", strict)
        flag("--with-subcharts", withSubcharts)
    }
}
