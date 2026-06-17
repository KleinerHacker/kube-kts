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

import org.pcsoft.framework.kube.kts.cli.commands.helm.HelmValueFileOptions
import picocli.CommandLine.Mixin
import java.nio.file.Path

/**
 * Abstract base class for compile-related commands in the CLI.
 *
 * This sealed class extends [BaseRootCommand] and provides functionality for handling values files
 * used during Kotlin script execution. The `-f`/`--values` option is provided by the
 * [HelmValueFileOptions] mixin, which is shared with the Helm commands so the same files can be
 * forwarded to Helm as well.
 */
sealed class BaseCompileCommand : BaseRootCommand() {
    @Mixin
    protected lateinit var valueFileOptions: HelmValueFileOptions

    /**
     * Array of validated Path objects representing the values files specified via command line.
     *
     * Delegates to [HelmValueFileOptions.values], which converts the raw strings into Path objects
     * and verifies that each file exists. If no values are provided, returns an empty array.
     */
    protected val values: Array<Path>
        get() = valueFileOptions.values
}