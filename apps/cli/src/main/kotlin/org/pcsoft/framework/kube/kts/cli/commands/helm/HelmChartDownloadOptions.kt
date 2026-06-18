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

package org.pcsoft.framework.kube.kts.cli.commands.helm

import org.pcsoft.framework.kube.kts.cli.intern.NoArgs
import org.pcsoft.framework.kube.kts.cli.intern.utils.HELM_MARKER
import picocli.CommandLine.Option

/**
 * Helm chart download, authentication and verification flags shared by the `pull` and `show` commands.
 *
 * Mirrors [HelmChartSourceOptions] but without `--dependency-update` (which only applies to commands
 * that render/install a chart). Embedded via picocli `@Mixin`; only the flags provided by the user are
 * forwarded to Helm.
 */
@NoArgs
class HelmChartDownloadOptions(
    @field:Option(names = ["--repo"], description = ["$HELM_MARKER chart repository url where to locate the requested chart"], paramLabel = "URL")
    var repo: String? = null,
    @field:Option(names = ["--username"], description = ["$HELM_MARKER chart repository username where to locate the requested chart"], paramLabel = "USER")
    var username: String? = null,
    @field:Option(names = ["--password"], description = ["$HELM_MARKER chart repository password where to locate the requested chart"], paramLabel = "PASSWORD")
    var password: String? = null,
    @field:Option(names = ["--pass-credentials"], description = ["$HELM_MARKER pass credentials to all domains"])
    var passCredentials: Boolean = false,
    @field:Option(names = ["--ca-file"], description = ["$HELM_MARKER verify certificates of HTTPS-enabled servers using this CA bundle"], paramLabel = "FILE")
    var caFile: String? = null,
    @field:Option(names = ["--cert-file"], description = ["$HELM_MARKER identify HTTPS client using this SSL certificate file"], paramLabel = "FILE")
    var certFile: String? = null,
    @field:Option(names = ["--key-file"], description = ["$HELM_MARKER identify HTTPS client using this SSL key file"], paramLabel = "FILE")
    var keyFile: String? = null,
    @field:Option(names = ["--insecure-skip-tls-verify"], description = ["$HELM_MARKER skip tls certificate checks for the chart download"])
    var insecureSkipTlsVerify: Boolean = false,
    @field:Option(names = ["--plain-http"], description = ["$HELM_MARKER use insecure HTTP connections for the chart download"])
    var plainHttp: Boolean = false,
    @field:Option(names = ["--keyring"], description = ["$HELM_MARKER location of public keys used for verification"], paramLabel = "FILE")
    var keyring: String? = null,
    @field:Option(names = ["--verify"], description = ["$HELM_MARKER verify the package before using it"])
    var verify: Boolean = false,
    @field:Option(names = ["--version"], description = ["$HELM_MARKER specify a version constraint for the chart version to use"], paramLabel = "VERSION")
    var version: String? = null,
    @field:Option(names = ["--devel"], description = ["$HELM_MARKER use development versions, too (equivalent to version '>0.0.0-0')"])
    var devel: Boolean = false,
) : HelmArgsProvider {
    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--repo", repo)
        opt("--username", username)
        opt("--password", password)
        flag("--pass-credentials", passCredentials)
        opt("--ca-file", caFile)
        opt("--cert-file", certFile)
        opt("--key-file", keyFile)
        flag("--insecure-skip-tls-verify", insecureSkipTlsVerify)
        flag("--plain-http", plainHttp)
        opt("--keyring", keyring)
        flag("--verify", verify)
        opt("--version", version)
        flag("--devel", devel)
    }
}
