package org.pcsoft.framework.kube.kts.definition.compiler

import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.types.MetadataSpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import java.io.File
import java.net.JarURLConnection
import java.net.URL
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.JvmScriptEvaluationConfigurationBuilder.Companion.invoke
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

object KubeKtsCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports("${ChartSpec::class.java.packageName}.*")
    defaultImports("${ResourceSpec::class.java.packageName}.*")
    defaultImports("${PortSpec::class.java.packageName}.*")
    defaultImports("${TemplateSpec::class.java.packageName}.*")
    defaultImports("${MetadataSpec::class.java.packageName}.*")
    defaultImports("${KubeVersion::class.java.packageName}.*")
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)

        val keyResource = KubeKtsCompilationConfiguration::class.java.name.replace('.', '/') + ".class"
        val thisJarFile = KubeKtsCompilationConfiguration::class.java.classLoader.getResource(keyResource)?.toContainingJarOrNull()
        if (thisJarFile != null) {
            dependenciesFromClassContext(
                KubeKtsCompilationConfiguration::class,
                thisJarFile.name, "kotlin-stdlib", "kotlin-reflect", "kotlin-scripting-dependencies"
            )
        } else {
            dependenciesFromClassContext(KubeKtsCompilationConfiguration::class, wholeClasspath = true)
        }
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
})

internal fun URL.toContainingJarOrNull(): File? =
    if (protocol == "jar") {
        (openConnection() as? JarURLConnection)?.jarFileURL?.toFileOrNull()
    } else null

internal fun URL.toFileOrNull() =
    try {
        File(toURI())
    } catch (e: IllegalArgumentException) {
        null
    } catch (e: java.net.URISyntaxException) {
        null
    } ?: run {
        if (protocol != "file") null
        else File(file)
    }