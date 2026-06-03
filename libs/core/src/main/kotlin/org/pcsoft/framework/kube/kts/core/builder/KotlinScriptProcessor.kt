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

package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import java.nio.file.Path
import kotlin.script.experimental.api.CompiledScript

/**
 * Interface for processing and executing Kotlin scripts.
 * Provides functionality to compile source scripts into executable scripts
 * and execute those compiled scripts with specified parameters.
 */
interface KotlinScriptProcessor {
    companion object {
        /**
         * Default implementation of the [KotlinScriptProcessor] interface.
         *
         * The [DEFAULT] processor provides the standard functionality for
         * compiling and executing Kotlin scripts using the default configuration.
         * It makes use of the [DefaultKotlinScriptProcessor] as its concrete implementation.
         *
         * This instance can be used as a ready-to-use script processor for tasks
         * such as compiling unprocessed Kotlin scripts into executable artifacts
         * and running them with specified parameters and value access strategies.
         */
        val DEFAULT: KotlinScriptProcessor = DefaultKotlinScriptProcessor
    }

    /**
     * Compiles the specified Kotlin script into an executable format.
     *
     * @param name The name of the script, typically used for identifying the compiled artifact.
     * @param script The file path to the Kotlin script to be compiled.
     * @param libScripts Paths to library scripts whose definitions are made available during compilation.
     * @param unsafe If true, enables unsafe script compilation, potentially bypassing security checks.
     * @return An [Either] instance containing the compiled script if successful or an error if the compilation fails.
     */
    fun compile(name: String, script: Path, libScripts: List<Path>, unsafe: Boolean): Either<CompiledScript>

    /**
     * Executes a compiled script with the specified parameters and value access strategy.
     *
     * @param name The name of the script execution context, typically used for logging or identification purposes.
     * @param script The compiled script to be executed.
     * @param valueAccess The mechanism for providing and retrieving values used during script execution.
     * @return An [Either] instance containing the result of the script execution if successful,
     * or an error if the execution fails.
     */
    fun <T> execute(name: String, script: CompiledScript, valueAccess: ValueAccess): Either<T>
}