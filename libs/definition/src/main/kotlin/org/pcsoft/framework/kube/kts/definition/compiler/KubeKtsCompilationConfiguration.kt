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
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
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

        val thisJarFile = getJarFromClass(KubeKtsCompilationConfiguration::class)
        val apiJarFile = getJarFromClass(ChartSpec::class)
        println("This jar file: $thisJarFile")
        println("API jar file: $apiJarFile")
        if (thisJarFile != null && apiJarFile != null) {
            dependenciesFromClassContext(
                KubeKtsCompilationConfiguration::class,
                thisJarFile.name, "kotlin-stdlib", "kotlin-reflect", "kotlin-scripting-dependencies", apiJarFile.name
            )
        } else {
            dependenciesFromClassContext(KubeKtsCompilationConfiguration::class, wholeClasspath = true)
        }
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
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