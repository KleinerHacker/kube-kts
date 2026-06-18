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
 * Command to run the tests of a named release.
 *
 * Operates on an already installed release; forwarded directly to `helm test <RELEASE>`.
 */
@Command(name = "test", description = ["Run tests for a release with helm"])
class TestCommand : BaseDirectHelmCommand(), HelmArgsProvider {
    @Parameters(index = "0", paramLabel = "RELEASE", description = ["Name of the release to test (forwarded to helm as positional RELEASE)"])
    private lateinit var release: String

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions

    @Option(names = ["--filter"], description = ["$HELM_MARKER specify tests by attribute (currently 'name') using attribute=value syntax or '!attribute=value' to exclude a test (repeatable)"], paramLabel = "KEY=VALUE")
    private var filter: Array<String>? = null
    @Option(names = ["--hide-notes"], description = ["$HELM_MARKER if set, do not show notes in test output. Does not affect presence in chart metadata"])
    private var hideNotes: Boolean = false
    @Option(names = ["--logs"], description = ["$HELM_MARKER dump the logs from test pods (this runs after all tests are complete, but before any cleanup)"])
    private var logs: Boolean = false
    @Option(names = ["--timeout"], description = ["$HELM_MARKER time to wait for any individual Kubernetes operation"], paramLabel = "DURATION")
    private var timeout: String? = null

    override val helmCommand: List<String>
        get() = listOf("test", release)

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        multi("--filter", filter)
        flag("--hide-notes", hideNotes)
        flag("--logs", logs)
        opt("--timeout", timeout)
    }
}
