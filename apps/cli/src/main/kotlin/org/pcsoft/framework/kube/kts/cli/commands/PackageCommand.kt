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
 * Command to package a KTS based chart repository into a versioned chart archive.
 *
 * This command renders the KTS repository to Helm YAML and then executes `helm package .` against the
 * rendered chart, forwarding all supported Helm package flags. The REPOSITORY (and optional TARGET)
 * positionals are inherited from [BaseRenderedHelmCommand].
 */
@Command(name = "package", description = ["Package a KTS based chart repository into a chart archive with helm"])
class PackageCommand : BaseRenderedHelmCommand(), HelmArgsProvider {
    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--app-version"], description = ["$HELM_MARKER set the appVersion on the chart to this version"], paramLabel = "VERSION")
    private var appVersion: String? = null
    @Option(names = ["--version"], description = ["$HELM_MARKER set the version on the chart to this semver version"], paramLabel = "VERSION")
    private var version: String? = null
    @Option(names = ["-d", "--destination"], description = ["$HELM_MARKER location to write the chart"], paramLabel = "DIR")
    private var destination: String? = null
    @Option(names = ["-u", "--dependency-update"], description = ["$HELM_MARKER update dependencies from \"Chart.yaml\" to dir \"charts/\" before packaging"])
    private var dependencyUpdate: Boolean = false
    @Option(names = ["--sign"], description = ["$HELM_MARKER use a PGP private key to sign this package"])
    private var sign: Boolean = false
    @Option(names = ["--key"], description = ["$HELM_MARKER name of the key to use when signing. Used if --sign is true"], paramLabel = "NAME")
    private var key: String? = null
    @Option(names = ["--keyring"], description = ["$HELM_MARKER location of a public keyring"], paramLabel = "FILE")
    private var keyring: String? = null
    @Option(names = ["--pass-stdin"], description = ["$HELM_MARKER read PGP passphrase from stdin. Used if --sign is true"])
    private var passStdin: Boolean = false

    override val helmCommand: List<String>
        get() = listOf("package", ".")

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--app-version", appVersion)
        opt("--version", version)
        opt("--destination", destination)
        flag("--dependency-update", dependencyUpdate)
        flag("--sign", sign)
        opt("--key", key)
        opt("--keyring", keyring)
        flag("--pass-stdin", passStdin)
    }
}
