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
 * Command to add a chart repository.
 *
 * Forwarded directly to `helm repo add <NAME> <URL>`.
 */
@Command(name = "add", description = ["Add a chart repository with helm"])
class RepoAddCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "NAME", description = ["Name of the repository (forwarded to helm as positional NAME)"])
    private lateinit var name: String
    @Parameters(index = "1", paramLabel = "URL", description = ["URL of the repository (forwarded to helm as positional URL)"])
    private lateinit var url: String

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--username"], description = ["$HELM_MARKER chart repository username"], paramLabel = "USER")
    private var username: String? = null
    @Option(names = ["--password"], description = ["$HELM_MARKER chart repository password"], paramLabel = "PASSWORD")
    private var password: String? = null
    @Option(names = ["--pass-credentials"], description = ["$HELM_MARKER pass credentials to all domains"])
    private var passCredentials: Boolean = false
    @Option(names = ["--ca-file"], description = ["$HELM_MARKER verify certificates of HTTPS-enabled servers using this CA bundle"], paramLabel = "FILE")
    private var caFile: String? = null
    @Option(names = ["--cert-file"], description = ["$HELM_MARKER identify HTTPS client using this SSL certificate file"], paramLabel = "FILE")
    private var certFile: String? = null
    @Option(names = ["--key-file"], description = ["$HELM_MARKER identify HTTPS client using this SSL key file"], paramLabel = "FILE")
    private var keyFile: String? = null
    @Option(names = ["--insecure-skip-tls-verify"], description = ["$HELM_MARKER skip tls certificate checks for the repository"])
    private var insecureSkipTlsVerify: Boolean = false
    @Option(names = ["--no-update"], description = ["$HELM_MARKER ignore repository update if it exists (the default is to update the existing repository)"])
    private var noUpdate: Boolean = false
    @Option(names = ["--force-update"], description = ["$HELM_MARKER replace (overwrite) the repo if it already exists"])
    private var forceUpdate: Boolean = false
    @Option(names = ["--allow-deprecated-repos"], description = ["$HELM_MARKER by default, this command will not allow adding official repos that have been permanently deleted. This disables that behavior"])
    private var allowDeprecatedRepos: Boolean = false

    override val helmCommand: List<String>
        get() = listOf("repo", "add", name, url)

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--username", username)
        opt("--password", password)
        flag("--pass-credentials", passCredentials)
        opt("--ca-file", caFile)
        opt("--cert-file", certFile)
        opt("--key-file", keyFile)
        flag("--insecure-skip-tls-verify", insecureSkipTlsVerify)
        flag("--no-update", noUpdate)
        flag("--force-update", forceUpdate)
        flag("--allow-deprecated-repos", allowDeprecatedRepos)
    }
}
