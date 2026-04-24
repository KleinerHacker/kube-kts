package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import java.nio.file.Path
import kotlin.script.experimental.api.CompiledScript

interface KotlinScriptProcessor {
    companion object {
        val DEFAULT: KotlinScriptProcessor = DefaultKotlinScriptProcessor
    }

    fun compile(name: String, script: Path): Either<CompiledScript>
    fun <T> execute(name: String, script: CompiledScript): Either<T>
}