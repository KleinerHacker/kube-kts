package org.pcsoft.framework.kube.kts.core.builder

import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.incremental.util.Either
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import org.pcsoft.framework.kube.kts.core.intern.utils.toEffectiveString
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsCompilationConfiguration
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsEvaluationConfiguration
import org.pcsoft.framework.kube.kts.logging.failedStyle
import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.successStyle
import org.pcsoft.framework.kube.kts.logging.symbolSubProcess
import java.nio.file.Files
import java.nio.file.Path
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.valueOrThrow
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.util.isError
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

internal object DefaultKotlinScriptProcessor : KotlinScriptProcessor {
    private val importRegex = Regex("""(?m)^\s*import\s+""")

    private val logger = logger()

    private val scriptingHost = BasicJvmScriptingHost()
    private val compilerConfiguration = KubeKtsCompilationConfiguration

    override fun compile(name: String, script: Path, unsafe: Boolean): Either<CompiledScript> = runBlocking {
        logger.atDebug().log { "$symbolSubProcess Compile script: $name" }

        if (!unsafe) {
            require(!Files.readString(script).matches(importRegex)) {
                "Import statements are not allowed in Kube KTS scripts"
            }
        }

        val result = scriptingHost.compiler.invoke(script.toFile().toScriptSource(), compilerConfiguration)
        if (result.isError()) {
            logger.atTrace().log { "Detect compile errors for script: $name".failedStyle() }
            Either.Error(result.reports.toEffectiveString())
        } else {
            logger.atTrace().log { "No compile errors for script: $name".successStyle() }
            Either.Success(result.valueOrThrow())
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> execute(name: String, script: CompiledScript, valueAccess: ValueAccess): Either<T> = runBlocking {
        logger.atDebug().log { "$symbolSubProcess Execute script: $name" }

        val evaluationConfiguration = KubeKtsEvaluationConfiguration(valueAccess)
        val result = scriptingHost.evaluator.invoke(script, evaluationConfiguration)
        if (result.isError()) {
            logger.atTrace().log { "Detect execution errors for script: $name".failedStyle() }
            Either.Error(result.reports.toEffectiveString())
        } else {
            logger.atTrace().log { "No execution errors for script: $name".successStyle() }
            Either.Success((result.valueOrThrow().returnValue as ResultValue.Value).value as T)
        }
    }
}