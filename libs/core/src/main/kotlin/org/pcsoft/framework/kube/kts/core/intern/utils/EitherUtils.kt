package org.pcsoft.framework.kube.kts.core.intern.utils

import org.jetbrains.kotlin.incremental.util.Either

internal fun <T, R> Iterable<Either<T>>.thenMapWithError(mapper: (T) -> Either<R>): Iterable<Either<R>> = map {
    it as? Either.Error ?: mapper((it as Either.Success<T>).value)
}

internal fun <T, R> Iterable<Either<T>>.thenMap(mapper: (T) -> R): Iterable<Either<R>> = map {
    it as? Either.Error ?: Either.Success(mapper((it as Either.Success<T>).value))
}

internal fun <T> Iterable<Either<T>>.thenCollect(errorCombiner: (List<Either.Error>) -> Either.Error): Either<Iterable<T>> {
    val errors = filterIsInstance<Either.Error>()
    if (errors.isNotEmpty())
        return errorCombiner(errors)

    return Either.Success(this.map { (it as Either.Success<T>).value })
}

internal fun <T, R> Either<T>.map(mapper: (T) -> R): Either<R> = when (this) {
    is Either.Success -> Either.Success(mapper(value))
    is Either.Error -> this
}