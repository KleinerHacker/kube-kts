package org.pcsoft.framework.kube.kts.api.utils

import org.junit.jupiter.api.Assertions

object KotlinAssertions {
    inline fun <reified T> assertInstanceOf(actual: Any, andThen: (T) -> Unit) {
        Assertions.assertInstanceOf(T::class.java, actual)
        andThen(actual as T)
    }

    fun <T> assertList(expectedSize: Int, actual: Iterable<T>, visitor: (T, Int) -> Unit) {
        Assertions.assertEquals(expectedSize, actual.count())

        for ((index, item) in actual.withIndex()) {
            visitor(item, index)
        }
    }

    fun <T> assertList(expectedSize: Int, actual: Iterable<T>, visitor: (T) -> Unit) =
        assertList(expectedSize, actual.asIterable()) { item, _ -> visitor(item) }

    fun <T> assertNotNull(actual: T?, andThen: (T) -> Unit) {
        Assertions.assertNotNull(actual)
        andThen(actual!!)
    }
}