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
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Tests for the `Either` helpers that carry compilation results through the rendering pipeline.
 *
 * The pipeline processes many scripts at once and has to keep going after a failure, collecting all
 * errors instead of aborting on the first one. These helpers implement exactly that behaviour: a value
 * keeps flowing through the mappers, while an error is passed along untouched.
 */
class EitherUtilsTest {

    /**
     * Verifies that mapping a successful value applies the mapper.
     */
    @Test
    fun testMapSuccess() {
        val result = (Either.Success(2) as Either<Int>).map { it * 3 }
        assertEquals(6, assertIs<Either.Success<Int>>(result).value)
    }

    /**
     * Verifies that mapping an error leaves it untouched.
     *
     * The mapper must not run, otherwise a failed script would be processed as if it had compiled.
     */
    @Test
    fun testMapError() {
        val error: Either<Int> = Either.Error("broken")
        val result = error.map { throw IllegalStateException("must not be called") }
        assertEquals("broken", assertIs<Either.Error>(result).reason)
    }

    /**
     * Verifies that mapping a list applies the mapper only to the successful entries.
     */
    @Test
    fun testThenMap() {
        val input: List<Either<Int>> = listOf(Either.Success(1), Either.Error("broken"), Either.Success(3))

        val result = input.thenMap { it * 10 }.toList()

        assertEquals(10, assertIs<Either.Success<Int>>(result[0]).value)
        assertEquals("broken", assertIs<Either.Error>(result[1]).reason)
        assertEquals(30, assertIs<Either.Success<Int>>(result[2]).value)
    }

    /**
     * Verifies that a mapper returning an error turns a previously successful entry into an error.
     */
    @Test
    fun testThenMapWithError() {
        val input: List<Either<Int>> = listOf(Either.Success(1), Either.Error("first"))

        val result = input.thenMapWithError { Either.Error("mapped $it") as Either<Int> }.toList()

        assertEquals("mapped 1", assertIs<Either.Error>(result[0]).reason)
        assertEquals("first", assertIs<Either.Error>(result[1]).reason)
    }

    /**
     * Verifies that collecting a list of successes yields a single success carrying all values.
     */
    @Test
    fun testThenCollectAllSuccess() {
        val input: List<Either<Int>> = listOf(Either.Success(1), Either.Success(2))

        val result = input.thenCollect { Either.Error("unused") }

        assertEquals(listOf(1, 2), assertIs<Either.Success<Iterable<Int>>>(result).value.toList())
    }

    /**
     * Verifies that collecting a list containing errors combines all of them.
     *
     * The combiner receives every error, which is what allows the CLI to report all broken scripts in
     * one run rather than one per invocation.
     */
    @Test
    fun testThenCollectWithErrors() {
        val input: List<Either<Int>> = listOf(Either.Success(1), Either.Error("a"), Either.Error("b"))

        val result = input.thenCollect { errors ->
            Either.Error(errors.joinToString(", ") { it.reason })
        }

        assertEquals("a, b", assertIs<Either.Error>(result).reason)
    }
}
