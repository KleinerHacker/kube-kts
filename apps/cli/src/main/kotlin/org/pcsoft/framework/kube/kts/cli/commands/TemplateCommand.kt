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

import org.pcsoft.framework.kube.kts.cli.commands.helm.*
import org.pcsoft.framework.kube.kts.cli.intern.utils.HELM_MARKER
import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option

/**
 * Command to generate a template for a KTS-based Helm chart repository.
 *
 * Renders the KTS repository to Helm YAML and executes `helm template`, forwarding all supported
 * Helm template flags. The release name is passed via `--name` (the `-n` shorthand is reserved for
 * `--namespace` to stay in sync with Helm).
 */
@Command(name = "template", description = ["Print template of a KTS based chart repository with helm"])
class TemplateCommand : BaseHelmCommand(), HelmArgsProvider {
    @Option(names = ["--name"], description = ["Name of the release (forwarded to helm as positional NAME)"], paramLabel = "NAME")
    private var name: String? = null

    @Mixin
    private lateinit var globalOptions: HelmGlobalOptions
    @Mixin
    private lateinit var valueOptions: HelmValueOptions
    @Mixin
    private lateinit var chartSourceOptions: HelmChartSourceOptions
    @Mixin
    private lateinit var renderSharedOptions: HelmRenderSharedOptions

    @Option(names = ["-a", "--api-versions"], description = ["$HELM_MARKER Kubernetes api versions used for Capabilities.APIVersions (repeatable)"], paramLabel = "VERSIONS")
    private var apiVersions: Array<String>? = null
    @Option(names = ["--include-crds"], description = ["$HELM_MARKER include CRDs in the templated output"])
    private var includeCrds: Boolean = false
    @Option(names = ["--is-upgrade"], description = ["$HELM_MARKER set .Release.IsUpgrade instead of .Release.IsInstall"])
    private var isUpgrade: Boolean = false
    @Option(names = ["--kube-version"], description = ["$HELM_MARKER Kubernetes version used for Capabilities.KubeVersion"], paramLabel = "VERSION")
    private var kubeVersion: String? = null
    @Option(names = ["--output-dir"], description = ["$HELM_MARKER writes the executed templates to files in output-dir instead of stdout"], paramLabel = "DIR")
    private var outputDir: String? = null
    @Option(names = ["-s", "--show-only"], description = ["$HELM_MARKER only show manifests rendered from the given templates (repeatable)"], paramLabel = "TEMPLATE")
    private var showOnly: Array<String>? = null
    @Option(names = ["--skip-tests"], description = ["$HELM_MARKER skip tests from templated output"])
    private var skipTests: Boolean = false
    @Option(names = ["--validate"], description = ["$HELM_MARKER validate your manifests against the Kubernetes cluster you are currently pointing at"])
    private var validate: Boolean = false
    @Option(names = ["--dry-run"], description = ["$HELM_MARKER simulate an install"])
    private var dryRun: Boolean = false
    @Option(names = ["-g", "--generate-name"], description = ["$HELM_MARKER generate the name (and omit the NAME parameter)"])
    private var generateName: Boolean = false
    @Option(names = ["-l", "--labels"], description = ["$HELM_MARKER labels that would be added to the release metadata (repeatable)"], paramLabel = "KEY=VALUE")
    private var labels: Array<String>? = null
    @Option(names = ["--create-namespace"], description = ["$HELM_MARKER create the release namespace if not present"])
    private var createNamespace: Boolean = false

    override val helmCommand: List<String>
        get() = buildList {
            add("template")
            name?.let { add(it) }
            add(".")
        }

    override val helmOptionGroups: List<HelmArgsProvider>
        get() = listOf(globalOptions, valueOptions, chartSourceOptions, renderSharedOptions, this)

    override fun toHelmArgs(): List<String> = helmArgs {
        multi("--api-versions", apiVersions)
        flag("--include-crds", includeCrds)
        flag("--is-upgrade", isUpgrade)
        opt("--kube-version", kubeVersion)
        opt("--output-dir", outputDir)
        multi("--show-only", showOnly)
        flag("--skip-tests", skipTests)
        flag("--validate", validate)
        flag("--dry-run", dryRun)
        flag("--generate-name", generateName)
        multi("--labels", labels)
        flag("--create-namespace", createNamespace)
    }
}
