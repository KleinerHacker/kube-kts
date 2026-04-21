package org.pcsoft.framework.kube.kts.core.builder

import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.incremental.util.Either
import org.pcsoft.framework.kube.kts.core.intern.utils.toEffectiveString
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsCompilationConfiguration
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsEvaluationConfiguration
import java.nio.file.Path
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.valueOrThrow
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.util.isError
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

internal object DefaultKotlinScriptProcessor : KotlinScriptProcessor {
    private val scriptingHost = BasicJvmScriptingHost()
    private val compilerConfiguration = KubeKtsCompilationConfiguration
    private val evaluationConfiguration = KubeKtsEvaluationConfiguration

    override fun compile(script: Path): Either<CompiledScript> = runBlocking {
        val result = scriptingHost.compiler.invoke(script.toFile().toScriptSource(), compilerConfiguration)
        if (result.isError())
            Either.Error(result.reports.toEffectiveString())
        else
            Either.Success(result.valueOrThrow())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> execute(script: CompiledScript): Either<T> = runBlocking {
        val result = scriptingHost.evaluator.invoke(script, evaluationConfiguration)
        if (result.isError())
            Either.Error(result.reports.toEffectiveString())
        else
            Either.Success((result.valueOrThrow().returnValue as ResultValue.Value).value as T)
    }
}