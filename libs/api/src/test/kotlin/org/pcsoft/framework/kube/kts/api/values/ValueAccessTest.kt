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

package org.pcsoft.framework.kube.kts.api.values

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.utils.KotlinAssertions
import tools.jackson.databind.JsonNode
import tools.jackson.databind.exc.JsonNodeException
import tools.jackson.dataformat.yaml.YAMLMapper
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ValueAccessTest {
    companion object {
        private val node: JsonNode = YAMLMapper().readTree(
            Path.of(ValueAccessTest::class.java.getResource("/values.yaml").toURI())
        )
        private val access = ValueAccess.ofRoot(node)
    }

    @Test
    fun testValueSuccessfully() {
        KotlinAssertions.assertNotNull(access.value<Int>("object.child.value1")) {
            assertEquals(123, it)
        }
        KotlinAssertions.assertNotNull(access.value<String>("object.child.value2")) {
            assertEquals("test", it)
        }
    }

    @Test
    fun testValueFailed() {
        assertFailsWith<IllegalArgumentException> {
            access.value<Int>("object.child.value3")
        }
        assertNull(access.valueOrNull<String>("object.child.value3"))

        assertFailsWith<JsonNodeException> {
            access.value<Int>("object.child.value2")
        }
        assertFailsWith<IllegalArgumentException> {
            access.value<String>("array.items1")
        }
        assertFailsWith<JsonNodeException> {
            access.valueOrNull<Int>("object.child.value2")
        }
        assertFailsWith<IllegalArgumentException> {
            access.valueOrNull<String>("array.items1")
        }
    }

    @Test
    fun testArraySuccessfully() {
        access.array<Int>("array.items1") { index, value ->
            assertEquals(arrayOf(1, 2, 3)[index], value)
        }
        access.array<String>("array.items2") { index, value ->
            assertEquals(arrayOf("test1", "test2", "test3")[index], value)
        }
        access.array("array.items3") { value ->
            assertEquals("name", value.value<String>("name"))
            assertEquals(123, value.value<Int>("value"))
        }
    }

    @Test
    fun testArrayFailed() {
        assertFailsWith<IllegalArgumentException> {
            access.array<Int>("array.items99")
        }
        assertNull(access.arrayOrNull<String>("array.items99"))
        access.arrayOrNull<String>("array.items99") { _, _ ->
            fail("Should not be called")
        }
        access.arrayOrNull("array.items99") { _, _ ->
            fail("Should not be called")
        }

        assertFailsWith<JsonNodeException> {
            access.array<Int>("array.items2")
        }
        assertFailsWith<IllegalArgumentException> {
            access.array<String>("object.child.value2")
        }
        assertFailsWith<JsonNodeException> {
            access.arrayOrNull<Int>("array.items2")
        }
        assertFailsWith<IllegalArgumentException> {
            access.arrayOrNull<String>("object.child.value2")
        }
    }

    @Test
    fun testMapSuccessfully() {
        access.map<Int>("map.items1") { key, value ->
            assertEquals(mapOf("key1" to 1, "key2" to 2)[key], value)
        }
        access.map<String>("map.items2") { key, value ->
            assertEquals(mapOf("key1" to "test1", "key2" to "test2")[key], value)
        }
        access.map("map.items3") { _, value ->
            assertEquals("name", value.value<String>("name"))
            assertEquals(123, value.value<Int>("value"))
        }
    }

    @Test
    fun testMapFailed() {
        assertFailsWith<IllegalArgumentException> {
            access.map<Int>("map.items99")
        }
        assertNull(access.mapOrNull<String>("map.items99"))
        access.mapOrNull<String>("map.items99") { _, _ ->
            fail("Should not be called")
        }
        access.mapOrNull("map.items99") { _, _ ->
            fail("Should not be called")
        }

        assertFailsWith<JsonNodeException> {
            access.map<Int>("map.items2")
        }
        assertFailsWith<IllegalArgumentException> {
            access.map<String>("array.items1")
        }
        assertFailsWith<JsonNodeException> {
            access.mapOrNull<Int>("map.items2")
        }
        assertFailsWith<IllegalArgumentException> {
            access.mapOrNull<String>("array.items1")
        }
    }

    @Test
    fun testExists() {
        assertTrue(access.exists("object.child.value1"))
        assertTrue(access.exists("array.items1"))
        assertTrue(access.exists("map.items1"))
        assertFalse(access.exists("map.items99"))
        assertFalse(access.exists("array.items99"))
        assertFalse(access.exists("object.child.value99"))

        KotlinAssertions.assertCalled {
            access.exists("object.child.value1") {
                it.set(true)
            }
        }

        access.exists("object.child.value99") {
            fail("Should not be called")
        }
    }

}
