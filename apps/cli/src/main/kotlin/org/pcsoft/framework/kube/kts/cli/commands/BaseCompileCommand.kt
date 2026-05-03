/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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

import picocli.CommandLine.Option
import java.nio.file.Path
import kotlin.io.path.exists

/**
 * Abstract base class for compile-related commands in the CLI.
 *
 * This sealed class extends [BaseRootCommand] and provides functionality for handling values files
 * used during Kotlin script execution. It manages the conversion of raw file paths to validated Path objects,
 * ensuring that specified values files exist before processing begins.
 */
sealed class BaseCompileCommand : BaseRootCommand() {
    @Option(names = ["-f", "--values"], description = ["Values file to use while execute Kotlin Scripts"], paramLabel = "FILE")
    private var rawValues: Array<String>? = null

    /**
     * Array of validated Path objects representing the values files specified via command line.
     *
     * This property is lazily initialized by converting [rawValues] strings into Path objects,
     * verifying that each file exists. If no values are provided, returns an empty array.
     * The validation ensures all specified values files exist before they're used in script execution.
     */
    protected val values: Array<Path> by lazy {
        rawValues?.map {
            Path.of(it).apply {
                require(exists()) { "Values file $it not found" }
            }
        }?.toTypedArray() ?: emptyArray()
    }
}