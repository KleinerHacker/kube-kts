/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.definition

import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsLibCompilationConfiguration
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsLibEvaluationConfiguration
import kotlin.script.experimental.annotations.KotlinScript

/**
 * Abstract base class for Kube KTS library scripts (`*.lib.kts`).
 *
 * Library scripts hold shared Kotlin functions that are automatically visible in every spec script
 * of the same `helm` directory. They use the same default imports as spec scripts but — unlike
 * [SpecTemplate] — do not receive an implicit value receiver, because they are compiled as
 * dependencies of the including spec script.
 *
 * Annotations:
 * - `@KotlinScript`: binds the compilation and evaluation configuration and declares the file path
 *   pattern by which a library script is recognised.
 *
 * Suppression:
 * - Annotated with `@Suppress("unused")`, since the class is only referenced by the scripting host.
 */
@Suppress("unused")
@KotlinScript(
    compilationConfiguration = KubeKtsLibCompilationConfiguration::class,
    evaluationConfiguration = KubeKtsLibEvaluationConfiguration::class,
    displayName = "Kube KTS Library",
    filePathPattern = ".*[/\\\\]helm[/\\\\].*\\.lib\\.kts"
)
abstract class LibTemplate
