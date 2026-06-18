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
 * Command to log in to an OCI registry.
 *
 * Forwarded directly to `helm registry login <HOST>`.
 */
@Command(name = "login", description = ["Login to a registry with helm"])
class RegistryLoginCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "HOST", description = ["Registry host to log in to (forwarded to helm as positional HOST)"])
    private lateinit var host: String

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["-u", "--username"], description = ["$HELM_MARKER registry username"], paramLabel = "USER")
    private var username: String? = null
    @Option(names = ["-p", "--password"], description = ["$HELM_MARKER registry password or identity token"], paramLabel = "PASSWORD")
    private var password: String? = null
    @Option(names = ["--password-stdin"], description = ["$HELM_MARKER read password or identity token from stdin"])
    private var passwordStdin: Boolean = false
    @Option(names = ["--insecure"], description = ["$HELM_MARKER allow connections to TLS registry without certs"])
    private var insecure: Boolean = false
    @Option(names = ["--ca-file"], description = ["$HELM_MARKER verify certificates of HTTPS-enabled servers using this CA bundle"], paramLabel = "FILE")
    private var caFile: String? = null
    @Option(names = ["--cert-file"], description = ["$HELM_MARKER identify registry client using this SSL certificate file"], paramLabel = "FILE")
    private var certFile: String? = null
    @Option(names = ["--key-file"], description = ["$HELM_MARKER identify registry client using this SSL key file"], paramLabel = "FILE")
    private var keyFile: String? = null
    @Option(names = ["--plain-http"], description = ["$HELM_MARKER use insecure HTTP connections for the registry login"])
    private var plainHttp: Boolean = false

    override val helmCommand: List<String>
        get() = listOf("registry", "login", host)

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--username", username)
        opt("--password", password)
        flag("--password-stdin", passwordStdin)
        flag("--insecure", insecure)
        opt("--ca-file", caFile)
        opt("--cert-file", certFile)
        opt("--key-file", keyFile)
        flag("--plain-http", plainHttp)
    }
}
