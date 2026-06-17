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
 * Helm rendering flags shared by the install and template commands.
 *
 * Embedded via picocli `@Mixin`; only the flags provided by the user are forwarded to Helm.
 */
@NoArgs
class HelmRenderSharedOptions(
    @field:Option(names = ["--no-hooks"], description = ["$HELM_MARKER prevent hooks from running during the operation"])
    var noHooks: Boolean = false,
    @field:Option(names = ["--disable-openapi-validation"], description = ["$HELM_MARKER the rendered manifests will not be validated against the Kubernetes OpenAPI Schema"])
    var disableOpenapiValidation: Boolean = false,
    @field:Option(names = ["--name-template"], description = ["$HELM_MARKER specify template used to name the release"], paramLabel = "TEMPLATE")
    var nameTemplate: String? = null,
    @field:Option(names = ["--render-subchart-notes"], description = ["$HELM_MARKER render subchart notes along with the parent"])
    var renderSubchartNotes: Boolean = false,
    @field:Option(names = ["--skip-crds"], description = ["$HELM_MARKER if set, no CRDs will be installed"])
    var skipCrds: Boolean = false,
    @field:Option(names = ["--post-renderer"], description = ["$HELM_MARKER the path to an executable to be used for post rendering"], paramLabel = "PATH")
    var postRenderer: String? = null,
    @field:Option(names = ["--post-renderer-args"], description = ["$HELM_MARKER an argument to the post-renderer (repeatable)"], paramLabel = "ARG")
    var postRendererArgs: Array<String>? = null,
    @field:Option(names = ["--timeout"], description = ["$HELM_MARKER time to wait for any individual Kubernetes operation (like Jobs for hooks)"], paramLabel = "DURATION")
    var timeout: String? = null,
) : HelmArgsProvider {
    override fun toHelmArgs(): List<String> = helmArgs {
        flag("--no-hooks", noHooks)
        flag("--disable-openapi-validation", disableOpenapiValidation)
        opt("--name-template", nameTemplate)
        flag("--render-subchart-notes", renderSubchartNotes)
        flag("--skip-crds", skipCrds)
        opt("--post-renderer", postRenderer)
        multi("--post-renderer-args", postRendererArgs)
        opt("--timeout", timeout)
    }
}
