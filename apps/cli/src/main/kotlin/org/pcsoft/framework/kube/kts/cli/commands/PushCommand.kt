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
 * Command to push a chart package to a remote (OCI) registry.
 *
 * Operates on an existing chart package, not on a KTS repository: the call is forwarded directly to
 * `helm push <CHART> <REMOTE>`.
 */
@Command(name = "push", description = ["Push a chart package to a remote registry with helm"])
class PushCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "CHART", description = ["Path to the packaged chart (forwarded to helm as positional CHART)"])
    private lateinit var chart: String
    @Parameters(index = "1", paramLabel = "REMOTE", description = ["Remote registry reference (forwarded to helm as positional REMOTE)"])
    private lateinit var remote: String

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--ca-file"], description = ["$HELM_MARKER verify certificates of HTTPS-enabled servers using this CA bundle"], paramLabel = "FILE")
    private var caFile: String? = null
    @Option(names = ["--cert-file"], description = ["$HELM_MARKER identify registry client using this SSL certificate file"], paramLabel = "FILE")
    private var certFile: String? = null
    @Option(names = ["--key-file"], description = ["$HELM_MARKER identify registry client using this SSL key file"], paramLabel = "FILE")
    private var keyFile: String? = null
    @Option(names = ["--insecure-skip-tls-verify"], description = ["$HELM_MARKER skip tls certificate checks for the chart upload"])
    private var insecureSkipTlsVerify: Boolean = false
    @Option(names = ["--plain-http"], description = ["$HELM_MARKER use insecure HTTP connections for the chart upload"])
    private var plainHttp: Boolean = false

    override val helmCommand: List<String>
        get() = listOf("push", chart, remote)

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--ca-file", caFile)
        opt("--cert-file", certFile)
        opt("--key-file", keyFile)
        flag("--insecure-skip-tls-verify", insecureSkipTlsVerify)
        flag("--plain-http", plainHttp)
    }
}
