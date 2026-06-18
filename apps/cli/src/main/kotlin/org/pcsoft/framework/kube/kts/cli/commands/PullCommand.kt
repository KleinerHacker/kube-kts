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
import org.pcsoft.framework.kube.kts.cli.commands.helm.HelmChartDownloadOptions
import org.pcsoft.framework.kube.kts.cli.commands.helm.HelmGlobalOptions
import org.pcsoft.framework.kube.kts.cli.intern.utils.HELM_MARKER
import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

/**
 * Command to download a chart from a repository.
 *
 * Operates on a remote chart, not on a KTS repository, so it neither renders nor needs a local
 * repository: the call is forwarded directly to `helm pull <CHART>`.
 */
@Command(name = "pull", aliases = ["fetch"], description = ["Download a chart from a repository with helm"])
class PullCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "CHART", description = ["Chart reference to download (forwarded to helm as positional CHART)"])
    private lateinit var chart: String

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions
    @Mixin
    private lateinit var downloadOptions: HelmChartDownloadOptions

    @Option(names = ["-d", "--destination"], description = ["$HELM_MARKER location to write the chart. If this and untardir are specified, untardir is appended to this"], paramLabel = "DIR")
    private var destination: String? = null
    @Option(names = ["--prov"], description = ["$HELM_MARKER fetch the provenance file, but don't perform verification"])
    private var prov: Boolean = false
    @Option(names = ["--untar"], description = ["$HELM_MARKER if set to true, will untar the chart after downloading it"])
    private var untar: Boolean = false
    @Option(names = ["--untardir"], description = ["$HELM_MARKER if untar is specified, this flag specifies the name of the directory into which the chart is expanded"], paramLabel = "DIR")
    private var untardir: String? = null

    override val helmCommand: List<String>
        get() = listOf("pull", chart)

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, downloadOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--destination", destination)
        flag("--prov", prov)
        flag("--untar", untar)
        opt("--untardir", untardir)
    }
}
