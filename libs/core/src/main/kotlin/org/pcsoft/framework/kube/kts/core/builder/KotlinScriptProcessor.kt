package org.pcsoft.framework.kube.kts.core.builder

import org.jetbrains.kotlin.incremental.util.Either
import kotlin.script.experimental.api.CompiledScript

interface KotlinScriptProcessor {
    companion object {
        val DEFAULT: KotlinScriptProcessor = DefaultKotlinScriptProcessor
    }

    fun compile(script: String) : Either<CompiledScript>
    fun <T>execute(script: CompiledScript) : Either<T>
}