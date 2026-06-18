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
 * Command to verify that a chart at a given path has been signed and is valid.
 *
 * Operates on a local chart package, not on a KTS repository: the call is forwarded directly to
 * `helm verify <PATH>`.
 */
@Command(name = "verify", description = ["Verify that a chart at the given path has been signed and is valid with helm"])
class VerifyCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "PATH", description = ["Path to the packaged chart to verify (forwarded to helm as positional PATH)"])
    private lateinit var path: String

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--keyring"], description = ["$HELM_MARKER keyring containing public keys"], paramLabel = "FILE")
    private var keyring: String? = null

    override val helmCommand: List<String>
        get() = listOf("verify", path)

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        opt("--keyring", keyring)
    }
}
