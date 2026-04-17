package org.pcsoft.framework.kube.kts.core.intern.utils

import org.jetbrains.kotlin.incremental.util.Either

fun <T> Either<T>.onSuccess(block: (T) -> Unit) {
    if (this is Either.Success)
        block(this.value)
}

fun <T> Either<T>.onFailure(block: (String) -> Unit) {
    if (this is Either.Error)
        block(this.reason)
}