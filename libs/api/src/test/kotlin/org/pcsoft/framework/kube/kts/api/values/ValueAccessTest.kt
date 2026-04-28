package org.pcsoft.framework.kube.kts.api.values

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.utils.KotlinAssertions
import tools.jackson.databind.JsonNode
import tools.jackson.databind.exc.JsonNodeException
import tools.jackson.dataformat.yaml.YAMLMapper
import java.nio.file.Path

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
            Assertions.assertEquals(123, it)
        }
        KotlinAssertions.assertNotNull(access.value<String>("object.child.value2")) {
            Assertions.assertEquals("test", it)
        }
    }

    @Test
    fun testValueFailed() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            access.value<Int>("object.child.value3")
        }
        Assertions.assertNull(access.valueOrNull<String>("object.child.value3"))

        Assertions.assertThrows(JsonNodeException::class.java) {
            access.value<Int>("object.child.value2")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            access.value<String>("array.items1")
        }
        Assertions.assertThrows(JsonNodeException::class.java) {
            access.valueOrNull<Int>("object.child.value2")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            access.valueOrNull<String>("array.items1")
        }
    }

    @Test
    fun testArraySuccessfully() {
        access.array<Int>("array.items1") { index, value ->
            Assertions.assertEquals(arrayOf(1,2,3)[index], value)
        }
        access.array<String>("array.items2") { index, value ->
            Assertions.assertEquals(arrayOf("test1", "test2", "test3")[index], value)
        }
        access.array("array.items3") { value ->
            Assertions.assertEquals("name", value.value<String>("name"))
            Assertions.assertEquals(123, value.value<Int>("value"))
        }
    }

    @Test
    fun testArrayFailed() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            access.array<Int>("array.items99")
        }
        Assertions.assertNull(access.arrayOrNull<String>("array.items99"))
        access.arrayOrNull<String>("array.items99") { _, _ ->
            Assertions.fail("Should not be called")
        }
        access.arrayOrNull("array.items99") { _, _ ->
            Assertions.fail("Should not be called")
        }

        Assertions.assertThrows(JsonNodeException::class.java) {
            access.array<Int>("array.items2")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            access.array<String>("object.child.value2")
        }
        Assertions.assertThrows(JsonNodeException::class.java) {
            access.arrayOrNull<Int>("array.items2")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            access.arrayOrNull<String>("object.child.value2")
        }
    }

    @Test
    fun testMapSuccessfully() {
        access.map<Int>("map.items1") { key, value ->
            Assertions.assertEquals(mapOf("key1" to 1, "key2" to 2)[key], value)
        }
        access.map<String>("map.items2") { key, value ->
            Assertions.assertEquals(mapOf("key1" to "test1", "key2" to "test2")[key], value)
        }
        access.map("map.items3") { _, value ->
            Assertions.assertEquals("name", value.value<String>("name"))
            Assertions.assertEquals(123, value.value<Int>("value"))
        }
    }

    @Test
    fun testMapFailed() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            access.map<Int>("map.items99")
        }
        Assertions.assertNull(access.mapOrNull<String>("map.items99"))
        access.mapOrNull<String>("map.items99") { _, _ ->
            Assertions.fail("Should not be called")
        }
        access.mapOrNull("map.items99") { _, _ ->
            Assertions.fail("Should not be called")
        }

        Assertions.assertThrows(JsonNodeException::class.java) {
            access.map<Int>("map.items2")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            access.map<String>("array.items1")
        }
        Assertions.assertThrows(JsonNodeException::class.java) {
            access.mapOrNull<Int>("map.items2")
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            access.mapOrNull<String>("array.items1")
        }
    }

    @Test
    fun testExists() {
        Assertions.assertTrue(access.exists("object.child.value1"))
        Assertions.assertTrue(access.exists("array.items1"))
        Assertions.assertTrue(access.exists("map.items1"))
        Assertions.assertFalse(access.exists("map.items99"))
        Assertions.assertFalse(access.exists("array.items99"))
        Assertions.assertFalse(access.exists("object.child.value99"))

        KotlinAssertions.assertCalled {
            access.exists("object.child.value1") {
                it.set(true)
            }
        }

        access.exists("object.child.value99") {
            Assertions.fail("Should not be called")
        }
    }

}