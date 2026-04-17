package org.pcsoft.framework.kube.kts.core.compiler

import kotlinx.coroutines.runBlocking
import org.pcsoft.framework.kube.kts.core.CompiledKubeKtsFile
import org.pcsoft.framework.kube.kts.core.PlainKubeKtsFile
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsCompilationConfiguration
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsEvaluationConfiguration
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.onFailure
import kotlin.script.experimental.api.valueOrNull
import kotlin.script.experimental.api.valueOrThrow
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

@KotlinScript(fileExtension = "chart.kts", compilationConfiguration = KubeKtsCompilationConfiguration::class, evaluationConfiguration = KubeKtsEvaluationConfiguration::class)
class KubeKtsCompiler {
    companion object {
        private val scriptingHost = BasicJvmScriptingHost()
        private val compilerConfiguration = KubeKtsCompilationConfiguration
        private val evaluationConfiguration = KubeKtsEvaluationConfiguration
    }

    fun compile(kubeFile: PlainKubeKtsFile) : CompiledKubeKtsFile = runBlocking {
        val script = scriptingHost.compiler.invoke(kubeFile.file.toFile().toScriptSource(), compilerConfiguration)
            .onFailure { throw IllegalStateException("KTS compilation failed: $it") }
            .valueOrThrow()

        CompiledKubeKtsFile(kubeFile, script)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T>execute(file: CompiledKubeKtsFile) : T = runBlocking {
        val result = scriptingHost.evaluator.invoke(file.compiledScript, evaluationConfiguration)
            .onFailure { throw IllegalStateException("KTS execution failed: $it") }
            .valueOrNull()

        (result?.returnValue as ResultValue.Value).value as T
    }
}