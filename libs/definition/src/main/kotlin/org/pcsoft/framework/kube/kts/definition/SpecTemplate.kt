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

package org.pcsoft.framework.kube.kts.definition

import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsSpecCompilationConfiguration
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsSpecEvaluationConfiguration
import kotlin.script.experimental.annotations.KotlinScript

/**
 * Abstract base class for Helm chart templates, providing Kotlin script-based configuration capabilities.
 *
 * This class is used to define templates for Helm charts in a structured and programmatic
 * manner using Kotlin scripting. It supports specific configurations for compilation
 * and evaluation through `KubeKtsCompilationConfiguration` and `KubeKtsEvaluationConfiguration`.
 *
 * The associated file path pattern ensures that scripts with a matching file path
 * are recognized and processed as chart templates.
 *
 * Annotations:
 * - `@KotlinScript`: Specifies the settings for script compilation and evaluation, along with
 *   the display name and the file path pattern for identifying related scripts.
 *
 * Suppression:
 * - Annotated with `@Suppress("unused")` to prevent IDE warnings for
 *   unused declarations, ensuring compatibility in various use cases.
 */
@Suppress("unused")
@KotlinScript(
    compilationConfiguration = KubeKtsSpecCompilationConfiguration::class,
    evaluationConfiguration = KubeKtsSpecEvaluationConfiguration::class,
    displayName = "Kube KTS Spec",
    filePathPattern = ".*[/\\\\]helm[/\\\\].*\\.spec\\.kts"
)
abstract class SpecTemplate