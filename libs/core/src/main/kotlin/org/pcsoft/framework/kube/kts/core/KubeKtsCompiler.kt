package org.pcsoft.framework.kube.kts.core

import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PortSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.types.MetadataSpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import java.io.File
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.onFailure
import kotlin.script.experimental.api.valueOrNull
import kotlin.script.experimental.host.StringScriptSource
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

class KubeKtsCompiler {
    companion object {
        private val scriptingHost = BasicJvmScriptingHost()
        private val compilerConfiguration = ScriptCompilationConfiguration {
            defaultImports("${ChartSpec::class.java.packageName}.*")
            defaultImports("${ResourceSpec::class.java.packageName}.*")
            defaultImports("${PortSpec::class.java.packageName}.*")
            defaultImports("${TemplateSpec::class.java.packageName}.*")
            defaultImports("${MetadataSpec::class.java.packageName}.*")
            defaultImports("${KubeVersion::class.java.packageName}.*")
            jvm {
                dependenciesFromCurrentContext(wholeClasspath = true)
            }
        }
        private val evaluationConfiguration = ScriptEvaluationConfiguration {
            jvm {

            }
        }
    }

    fun compileAndExecute(script: String) {
        val x = scriptingHost.eval(StringScriptSource(script), compilerConfiguration, evaluationConfiguration)
            .onFailure { throw IllegalStateException("KTS compilation failed: $it") }
            .valueOrNull()
        println(x?.returnValue)
    }

    fun compileAndExecute(script: File) {
        scriptingHost.eval(script.toScriptSource(), compilerConfiguration, evaluationConfiguration)
            .onFailure { throw IllegalStateException("KTS compilation failed: $it") }
            .valueOrNull()
    }
}