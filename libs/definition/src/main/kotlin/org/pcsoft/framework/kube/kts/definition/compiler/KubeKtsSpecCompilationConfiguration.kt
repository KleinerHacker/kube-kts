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
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.chart.types.MetadataBaseSpec
import org.pcsoft.framework.kube.kts.api.types.MailAddress
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import java.io.File
import java.net.JarURLConnection
import java.net.URL
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

/**
 * Compilation configuration for Kube KTS spec scripts (`*.spec.kts`).
 *
 * It makes the complete DSL of `libs/api` available without any `import` statement — which is
 * mandatory, because the script safety model forbids imports in spec scripts. On top of the
 * default imports it:
 * - resolves the JVM dependencies from the current class context so the DSL classes are on the
 *   script classpath,
 * - pins the JVM target of the compiled scripts to the one used by the modules,
 * - accepts scripts in every IDE location so IntelliJ resolves the DSL inside a `helm` directory,
 * - discovers the sibling `*.lib.kts` files before compiling and adds them as imported scripts,
 * - registers [ValueAccess] as implicit receiver, which enables the top level `value(...)`,
 *   `array(...)` and `exists(...)` calls.
 */
@Suppress("JavaIoSerializableObjectMustHaveReadResolve")
object KubeKtsSpecCompilationConfiguration : ScriptCompilationConfiguration({
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

    refineConfiguration {
        beforeCompiling { context ->
            // Lib scripts are compiled as dependencies of spec scripts — don't add importScripts
            // for them, otherwise they would try to import themselves (cycle).
            if (context.script.name?.endsWith(".lib.kts") == true) {
                return@beforeCompiling context.compilationConfiguration.asSuccess()
            }

            // Discover *.lib.kts siblings in the same helm/ tree so IntelliJ sees their members.
            // IntelliJ may not wrap the script as FileScriptSource — fall back to locationId.
            val scriptFile = (context.script as? FileScriptSource)?.file
                ?: context.script.locationId?.let { locId ->
                    runCatching { java.net.URI(locId).takeIf { it.scheme == "file" }?.let { File(it) } }
                        .getOrNull() ?: File(locId).takeIf { it.exists() }
                }
            val libFiles = findHelmRoot(scriptFile)
                ?.walkTopDown()
                ?.filter { it.isFile && it.name.endsWith(".lib.kts") }
                ?.toList()
                .orEmpty()

            if (libFiles.isEmpty()) {
                return@beforeCompiling context.compilationConfiguration.asSuccess()
            }

            ScriptCompilationConfiguration(context.compilationConfiguration) {
                importScripts.put(libFiles.map { it.toScriptSource() })
            }.asSuccess()
        }
    }

    implicitReceivers(KotlinType(ValueAccess::class))
})

internal fun findHelmRoot(file: File?): File? {
    file?.parentFile?.let { dir ->
        if (dir.name.equals("helm", true))
            return dir

        return findHelmRoot(dir)
    }

    return null
}

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