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
import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Parameters

/**
 * Command to show the README of a chart.
 *
 * Operates on a remote/local chart; forwarded directly to `helm show readme <CHART>`.
 */
@Command(name = "readme", description = ["Show the chart's README with helm"])
class ShowReadmeCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "CHART", description = ["Chart reference to inspect (forwarded to helm as positional CHART)"])
    private lateinit var chart: String

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions
    @Mixin
    private lateinit var downloadOptions: HelmChartDownloadOptions

    override val helmCommand: List<String>
        get() = listOf("show", "readme", chart)

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, downloadOptions, this)

    override fun toHelmArgs(): List<String> = emptyList()
}
