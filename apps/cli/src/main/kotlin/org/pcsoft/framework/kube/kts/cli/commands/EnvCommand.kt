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
import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Parameters

/**
 * Command to print all the Helm environment information.
 *
 * Does not operate on a release or chart: the call is forwarded directly to `helm env [NAME]`. If a
 * NAME is given, only that environment variable is printed.
 */
@Command(name = "env", description = ["Print all the helm environment information"])
class EnvCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "NAME", arity = "0..1", description = ["Name of a single environment variable to print (forwarded to helm as positional NAME)"])
    private var name: String? = null

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    override val helmCommand: List<String>
        get() = buildList {
            add("env")
            name?.let { add(it) }
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = emptyList()
}
