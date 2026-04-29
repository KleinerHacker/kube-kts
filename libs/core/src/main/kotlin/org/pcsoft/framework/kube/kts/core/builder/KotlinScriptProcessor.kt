package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import java.nio.file.Path
import kotlin.script.experimental.api.CompiledScript

interface KotlinScriptProcessor {
    companion object {
        val DEFAULT: KotlinScriptProcessor = DefaultKotlinScriptProcessor
    }

    fun compile(name: String, script: Path, unsafe: Boolean): Either<CompiledScript>
    fun <T> execute(name: String, script: CompiledScript, valueAccess: ValueAccess): Either<T>
}