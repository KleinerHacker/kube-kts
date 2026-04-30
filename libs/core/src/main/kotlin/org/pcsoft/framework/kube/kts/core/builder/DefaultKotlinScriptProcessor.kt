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
import kotlin.script.experimental.jvm.util.renderError
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

/**
 * Default implementation of the [KotlinScriptProcessor] interface.
 *
 * The `DefaultKotlinScriptProcessor` object provides functionality for compiling
 * Kotlin scripts into executable artifacts and executing those scripts with a specified
 * set of parameters and a value access strategy. It restricts the usage of certain script
 * features, like import statements, based on the `unsafe` flag during the compilation phase.
 *
 * It relies on a JVM scripting host, a predefined compilation configuration, and detailed logging
 * to ensure robust feedback during both script compilation and execution steps.
 *
 * ## Responsibilities:
 * - Validates and compiles Kotlin scripts into executable formats.
 * - Executes compiled scripts within a configurable execution context.
 *
 * ## Behavior:
 * - Compilation:
 *     - Denies the usage of `import` statements in scripts unless the `unsafe` flag is set to true.
 *     - Reports detailed errors in case of compilation failures.
 *
 * - Execution:
 *     - Executes scripts with an evaluation configuration tailored to the provided parameters.
 *     - Reports and processes errors encountered during execution.
 *
 * ## Properties:
 * - Utilizes a regex for matching and detecting import statements.
 * - Leverages a JVM scripting host for compiling and executing scripts.
 * - Uses a logging mechanism to trace script processing steps.
 *
 * This implementation ensures a secure and traceable scripting environment by adhering to the above behaviors.
 */
internal object DefaultKotlinScriptProcessor : KotlinScriptProcessor {
    private val importRegex = Regex("""(?m)^\s*import\s+""")

    private val logger = logger()

    private val scriptingHost = BasicJvmScriptingHost()
    private val compilerConfiguration = KubeKtsCompilationConfiguration

    override fun compile(name: String, script: Path, unsafe: Boolean): Either<CompiledScript> = runBlocking {
        logger.atDebug().log { "$symbolSubProcess Compile script: $name" }

        if (!unsafe) {
            require(importRegex.find(Files.readString(script)) == null) {
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
            logger.atTrace().log { "Detect errors for script: $name".failedStyle() }
            Either.Error(result.reports.toEffectiveString())
        } else if (result.valueOrThrow().returnValue is ResultValue.Error) {
            logger.atTrace().log { "Detect execution errors for script: $name".successStyle() }
            Either.Error((result.valueOrThrow().returnValue as ResultValue.Error).renderError())
        } else {
            logger.atTrace().log { "No execution errors for script: $name".successStyle() }
            Either.Success((result.valueOrThrow().returnValue as ResultValue.Value).value as T)
        }
    }
}