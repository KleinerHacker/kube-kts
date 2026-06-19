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

package org.pcsoft.framework.kube.kts.api.utils

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

object KotlinAssertions {
    inline fun <reified T> assertInstanceOf(actual: Any, andThen: (T) -> Unit) {
        val typed = assertIs<T>(actual)
        andThen(typed)
    }

    fun <T> assertList(expectedSize: Int, actual: Iterable<T>, visitor: (T, Int) -> Unit) {
        assertEquals(expectedSize, actual.count())

        for ((index, item) in actual.withIndex()) {
            visitor(item, index)
        }
    }

    fun <T> assertList(expectedSize: Int, actual: Iterable<T>, visitor: (T) -> Unit) =
        assertList(expectedSize, actual.asIterable()) { item, _ -> visitor(item) }

    fun <T> assertNotNull(actual: T?, andThen: (T) -> Unit) {
        val notNull = assertNotNull(actual)
        andThen(notNull)
    }

    fun assertCalled(action: (AtomicBoolean) -> Unit) {
        val executed = AtomicBoolean(false)
        action(executed)
        assertTrue(executed.get())
    }
}