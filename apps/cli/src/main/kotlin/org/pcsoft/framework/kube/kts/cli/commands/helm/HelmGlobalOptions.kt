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
 * Global Helm flags that are available to every Helm subcommand and forwarded unchanged to Helm.
 *
 * This option group is meant to be embedded via picocli `@Mixin` into all Helm based commands.
 * Only the flags actually provided by the user are translated to Helm arguments in [toHelmArgs].
 */
@NoArgs
class HelmGlobalOptions(
    @field:Option(names = ["-n", "--namespace"], description = ["$HELM_MARKER namespace scope for this request"], paramLabel = "NAMESPACE")
    var namespace: String? = null,
    @field:Option(names = ["--kube-context"], description = ["$HELM_MARKER name of the kubeconfig context to use"], paramLabel = "CONTEXT")
    var kubeContext: String? = null,
    @field:Option(names = ["--kubeconfig"], description = ["$HELM_MARKER path to the kubeconfig file"], paramLabel = "FILE")
    var kubeConfig: String? = null,
    @field:Option(names = ["--kube-apiserver"], description = ["$HELM_MARKER the address and the port for the Kubernetes API server"], paramLabel = "ADDRESS")
    var kubeApiServer: String? = null,
    @field:Option(names = ["--kube-as-user"], description = ["$HELM_MARKER username to impersonate for the operation"], paramLabel = "USER")
    var kubeAsUser: String? = null,
    @field:Option(names = ["--kube-as-group"], description = ["$HELM_MARKER group to impersonate for the operation (repeatable)"], paramLabel = "GROUP")
    var kubeAsGroup: Array<String>? = null,
    @field:Option(names = ["--kube-ca-file"], description = ["$HELM_MARKER the certificate authority file for the Kubernetes API server connection"], paramLabel = "FILE")
    var kubeCaFile: String? = null,
    @field:Option(names = ["--kube-token"], description = ["$HELM_MARKER bearer token used for authentication"], paramLabel = "TOKEN")
    var kubeToken: String? = null,
    @field:Option(names = ["--kube-tls-server-name"], description = ["$HELM_MARKER server name to use for Kubernetes API server certificate validation"], paramLabel = "NAME")
    var kubeTlsServerName: String? = null,
    @field:Option(names = ["--kube-insecure-skip-tls-verify"], description = ["$HELM_MARKER if true, the Kubernetes API server's certificate will not be checked for validity"])
    var kubeInsecureSkipTlsVerify: Boolean = false,
    @field:Option(names = ["--burst-limit"], description = ["$HELM_MARKER client-side default throttling limit"], paramLabel = "INT")
    var burstLimit: Int? = null,
    @field:Option(names = ["--qps"], description = ["$HELM_MARKER queries per second used when communicating with the Kubernetes API"], paramLabel = "QPS")
    var qps: String? = null,
    @field:Option(names = ["--registry-config"], description = ["$HELM_MARKER path to the registry config file"], paramLabel = "FILE")
    var registryConfig: String? = null,
    @field:Option(names = ["--repository-cache"], description = ["$HELM_MARKER path to the directory containing cached repository indexes"], paramLabel = "DIR")
    var repositoryCache: String? = null,
    @field:Option(names = ["--repository-config"], description = ["$HELM_MARKER path to the file containing repository names and URLs"], paramLabel = "FILE")
    var repositoryConfig: String? = null,
) : HelmArgsProvider {
    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--namespace", namespace)
        opt("--kube-context", kubeContext)
        opt("--kubeconfig", kubeConfig)
        opt("--kube-apiserver", kubeApiServer)
        opt("--kube-as-user", kubeAsUser)
        multi("--kube-as-group", kubeAsGroup)
        opt("--kube-ca-file", kubeCaFile)
        opt("--kube-token", kubeToken)
        opt("--kube-tls-server-name", kubeTlsServerName)
        flag("--kube-insecure-skip-tls-verify", kubeInsecureSkipTlsVerify)
        opt("--burst-limit", burstLimit)
        opt("--qps", qps)
        opt("--registry-config", registryConfig)
        opt("--repository-cache", repositoryCache)
        opt("--repository-config", repositoryConfig)
    }
}
