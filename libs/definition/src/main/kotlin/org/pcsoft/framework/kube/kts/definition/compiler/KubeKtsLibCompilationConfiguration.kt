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

package org.pcsoft.framework.kube.kts.definition.compiler

import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.DeploymentSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortMappingSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.types.MetadataBaseSpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.types.MailAddress
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

/**
 * Defines the compilation configuration for Kotlin scripts tailored to Kubernetes resource specifications.
 *
 * This configuration object is utilized to customize the behavior of the Kotlin scripting engine when
 * compiling scripts that interact with Kubernetes resources. It sets up the default imports, JVM dependencies,
 * compiler options, and Integrated Development Environment (IDE) settings to enable seamless scripting
 * for Kubernetes resource definitions.
 *
 * The configuration includes:
 * - Default imports for common Kubernetes-specification-related classes such as `ChartSpec`, `ResourceSpec`,
 *   `PortMappingSpec`, and others.
 * - Support for additional imports from standard Java libraries (e.g., `java.net.URL`, `java.time.*`) and Kotlin
 *   libraries (e.g., `kotlin.time.*`).
 * - JVM runtime dependencies derived from the context of the current application, ensuring that all necessary
 *   classes and libraries are available for script resolution.
 * - Compiler options to specify the target JVM version for script execution.
 * - IDE settings to accept scripts in all locations.
 * - Implicit receivers to provide access to higher-level abstractions like `ValueAccess`.
 *
 * This configuration serves as a foundational setup for scripts defining Kubernetes resources and their
 * respective configurations in a Kotlin Domain Specific Language (DSL).
 */
@Suppress("JavaIoSerializableObjectMustHaveReadResolve")
object KubeKtsLibCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports("${ChartSpec::class.java.packageName}.*")
    defaultImports("${ResourceSpec::class.java.packageName}.*")
    defaultImports("${PortMappingSpec::class.java.packageName}.*")
    defaultImports("${ExplicitTemplateSpec::class.java.packageName}.*")
    defaultImports("${MetadataBaseSpec::class.java.packageName}.*")
    defaultImports("${KubeVersion::class.java.packageName}.*")
    defaultImports("${MailAddress::class.java.packageName}.*")
    defaultImports("${ValueAccess::class.java.packageName}.*")

    defaultImports("${ServiceSpec::class.qualifiedName}.*")
    defaultImports("${DeploymentSpec::class.qualifiedName}.*")
    defaultImports("${PortMappingSpec::class.qualifiedName}.*")

    defaultImports("java.net.URL", "java.net.URI")
    defaultImports("java.time.*")
    defaultImports("kotlin.time.*", "kotlin.time.Duration.Companion.*")

    jvm {
        val thisJarFile = getJarFromClass(KubeKtsLibCompilationConfiguration::class)
        val apiJarFile = getJarFromClass(ChartSpec::class)
        if (thisJarFile != null && apiJarFile != null) {
            dependenciesFromClassContext(
                KubeKtsLibCompilationConfiguration::class,
                thisJarFile.name, "kotlin-stdlib", apiJarFile.name
            )
        } else {
            dependenciesFromClassContext(KubeKtsLibCompilationConfiguration::class, "kotlin-stdlib")
        }
    }

    compilerOptions.put(listOf("-jvm-target", "25"))

    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
})