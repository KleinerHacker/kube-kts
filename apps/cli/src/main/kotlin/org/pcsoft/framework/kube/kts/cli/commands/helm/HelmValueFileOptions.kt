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
import java.nio.file.Path
import kotlin.io.path.exists

/**
 * The `-f`/`--values` option holding the YAML values files.
 *
 * This option is dual-use: internally the files feed the Kotlin Script rendering (for all render
 * based commands), and for Helm commands the very same files are additionally forwarded to Helm as
 * `-f <file>`. It is embedded via picocli `@Mixin`.
 */
@NoArgs
class HelmValueFileOptions(
    @field:Option(
        names = ["-f", "--values"],
        description = ["$HELM_MARKER values file to use while executing Kotlin Scripts and rendering with Helm (repeatable)"],
        paramLabel = "FILE"
    )
    var rawValues: Array<String>? = null,
) : HelmArgsProvider {
    /**
     * The validated values file paths.
     *
     * Converts the raw option strings into [Path] objects and ensures each file exists. Returns an
     * empty array if no values files were provided.
     *
     * @throws IllegalArgumentException if a specified values file does not exist.
     */
    val values: Array<Path> by lazy {
        rawValues?.map {
            Path.of(it).apply {
                require(exists()) { "Values file $it not found" }
            }
        }?.toTypedArray() ?: emptyArray()
    }

    override fun toHelmArgs(): List<String> = helmArgs {
        values.forEach {
            add("-f")
            add(it.toString())
        }
    }
}
