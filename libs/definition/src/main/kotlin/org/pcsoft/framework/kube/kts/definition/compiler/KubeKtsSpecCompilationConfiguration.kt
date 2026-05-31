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

package org.pcsoft.framework.kube.kts.definition.compiler

import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.DeploymentSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ServiceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortMappingSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.types.MetadataBaseSpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.types.MailAddress
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import java.io.File
import java.net.JarURLConnection
import java.net.URL
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

class KubeKtsSpecCompilationConfiguration(libScripts: List<Path> = emptyList()) : ScriptCompilationConfiguration({
    defaultImports("${ChartSpec::class.java.packageName}.*")
    defaultImports("${ResourceSpec::class.java.packageName}.*")
    defaultImports("${PortMappingSpec::class.java.packageName}.*")
    defaultImports("${TemplateSpec::class.java.packageName}.*")
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
        dependenciesFromCurrentContext(wholeClasspath = true)

        val thisJarFile = getJarFromClass(KubeKtsSpecCompilationConfiguration::class)
        val apiJarFile = getJarFromClass(ChartSpec::class)
        if (thisJarFile != null && apiJarFile != null) {
            dependenciesFromClassContext(
                KubeKtsSpecCompilationConfiguration::class,
                thisJarFile.name, "kotlin-stdlib", "kotlin-reflect", "kotlin-scripting-dependencies", apiJarFile.name
            )
        } else {
            dependenciesFromClassContext(KubeKtsSpecCompilationConfiguration::class, wholeClasspath = true)
        }
    }

    compilerOptions.put(listOf("-jvm-target", "25"))

    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }

    if (libScripts.isNotEmpty()) {
        val libSources = libScripts.map { it.toFile().toScriptSource() }
        refineConfiguration {
            beforeCompiling { context ->
                // Only inject lib imports for spec scripts, not when compiling the lib scripts
                // themselves as dependencies — otherwise importScripts would self-reference.
                if (context.script.name?.endsWith(".lib.kts") == true) {
                    context.compilationConfiguration.asSuccess()
                } else {
                    ScriptCompilationConfiguration(context.compilationConfiguration) {
                        importScripts.put(libSources)
                    }.asSuccess()
                }
            }
        }
    }

    implicitReceivers(KotlinType(ValueAccess::class))
})

internal fun getJarFromClass(clazz: KClass<*>): File? {
    val keyResource = clazz.java.name.replace('.', '/') + ".class"
    return clazz.java.classLoader.getResource(keyResource)?.toContainingJarOrNull()
}

internal fun URL.toContainingJarOrNull(): File? =
    if (protocol == "jar") {
        (openConnection() as? JarURLConnection)?.jarFileURL?.toFileOrNull()
    } else null

internal fun URL.toFileOrNull() =
    try {
        File(toURI())
    } catch (_: IllegalArgumentException) {
        null
    } catch (_: java.net.URISyntaxException) {
        null
    } ?: run {
        if (protocol != "file") null
        else File(file)
    }