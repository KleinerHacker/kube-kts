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
 * Helm value override flags (the `--set` family) shared by the install, template and lint commands.
 *
 * Embedded via picocli `@Mixin`; every provided override is forwarded to Helm unchanged.
 */
@NoArgs
class HelmValueOptions(
    @field:Option(names = ["--set"], description = ["$HELM_MARKER set values on the command line (repeatable)"], paramLabel = "KEY=VALUE")
    var set: Array<String>? = null,
    @field:Option(names = ["--set-string"], description = ["$HELM_MARKER set STRING values on the command line (repeatable)"], paramLabel = "KEY=VALUE")
    var setString: Array<String>? = null,
    @field:Option(names = ["--set-file"], description = ["$HELM_MARKER set values from respective files (repeatable)"], paramLabel = "KEY=PATH")
    var setFile: Array<String>? = null,
    @field:Option(names = ["--set-json"], description = ["$HELM_MARKER set JSON values on the command line (repeatable)"], paramLabel = "KEY=JSON")
    var setJson: Array<String>? = null,
    @field:Option(names = ["--set-literal"], description = ["$HELM_MARKER set a literal STRING value on the command line (repeatable)"], paramLabel = "KEY=VALUE")
    var setLiteral: Array<String>? = null,
) : HelmArgsProvider {
    override fun toHelmArgs(): List<String> = helmArgs {
        multi("--set", set)
        multi("--set-string", setString)
        multi("--set-file", setFile)
        multi("--set-json", setJson)
        multi("--set-literal", setLiteral)
    }
}
