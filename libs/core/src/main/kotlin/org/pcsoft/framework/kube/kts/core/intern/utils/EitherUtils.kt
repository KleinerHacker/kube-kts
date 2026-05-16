/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

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