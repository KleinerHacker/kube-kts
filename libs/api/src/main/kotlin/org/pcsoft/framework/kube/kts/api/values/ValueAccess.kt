package org.pcsoft.framework.kube.kts.api.values

import tools.jackson.databind.JsonNode

@Suppress("UNCHECKED_CAST")
class ValueAccess private constructor(val node: JsonNode, val basePath: String?) {

    companion object {
        fun ofRoot(node: JsonNode): ValueAccess = ValueAccess(node, "values")
        fun ofChild(node: JsonNode): ValueAccess = ValueAccess(node, null)

        fun findNode(node: JsonNode, basePath: String?, key: String): JsonNode? {
            val pathSegments = if (basePath == null)
                key.split('.')
            else
                "$basePath.$key".split('.')

            var currentNode: JsonNode? = node
            for (segment in pathSegments) {
                currentNode = currentNode?.get(segment)
                if (currentNode == null || currentNode.isNull) {
                    return null
                }
            }
            return currentNode
        }

        inline fun <reified T> JsonNode.asValue(): T = when (T::class) {
            String::class -> asString() as? T ?: throw IllegalArgumentException("Cannot convert to String")
            Int::class -> asInt() as? T ?: throw IllegalArgumentException("Cannot convert to Int")
            Long::class -> asLong() as? T ?: throw IllegalArgumentException("Cannot convert to Long")
            Double::class -> asDouble() as? T ?: throw IllegalArgumentException("Cannot convert to Double")
            Short::class -> asInt().toShort() as? T ?: throw IllegalArgumentException("Cannot convert to Short")
            Float::class -> asDouble().toFloat() as? T ?: throw IllegalArgumentException("Cannot convert to Float")
            Byte::class -> asInt().toByte() as? T ?: throw IllegalArgumentException("Cannot convert to Byte")
            Boolean::class -> asBoolean() as? T ?: throw IllegalArgumentException("Cannot convert to Boolean")
            else -> throw IllegalArgumentException("Unsupported type ${T::class.qualifiedName}")
        }

        inline fun <reified T> JsonNode.asArray(): Array<T> =
            (0 until size()).map { i -> this[i].asValue<T>() }.toTypedArray()

        fun JsonNode.asArrayOfObject(): Array<ValueAccess> =
            (0 until size()).map { i -> ofChild(this[i]) }.toTypedArray()

        inline fun <reified T> JsonNode.asMap(): Map<String, T> =
            propertyNames().associateWith { this[it].asValue<T>() }

        fun JsonNode.asMapOfObject(): Map<String, ValueAccess> =
            propertyNames().associateWith { ofChild(this[it]) }
    }

    inline fun <reified T : Any> valueOrNull(key: String): T? {
        val currentNode = findNode(node, basePath, key) ?: return null

        if (!currentNode.isValueNode)
            throw IllegalArgumentException("Key '$key' is not a value node")

        return currentNode.asValue<T>()
    }

    fun valueOrNull(key: String, action: (ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: return
        action(ValueAccess(currentNode, basePath))
    }

    inline fun <reified T : Any> value(key: String): T {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (!currentNode.isValueNode)
            throw IllegalArgumentException("Key '$key' is not a value node")

        return currentNode.asValue<T>()
    }

    fun value(key: String, action: (ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")
        action(ValueAccess(currentNode, basePath))
    }

    inline fun <reified T : Any> arrayOrNull(key: String): Array<T>? {
        val currentNode = findNode(node, basePath, key) ?: return null

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        return jsonArray.asArray<T>()
    }

    inline fun <reified T : Any> arrayOrNull(key: String, action: (T) -> Unit) {
        arrayOrNull<T>(key) { _, value -> action(value) }
    }

    inline fun <reified T : Any> arrayOrNull(key: String, action: (Int, T) -> Unit) {
        val array = arrayOrNull<T>(key) ?: return
        array.forEachIndexed { index, item -> action(index, item) }
    }

    @JvmName("arrayOfObjectOrNull")
    fun arrayOrNull(key: String, action: (ValueAccess) -> Unit) {
        arrayOrNull(key) { _, access -> action(access) }
    }

    @JvmName("arrayOfObjectOrNull")
    fun arrayOrNull(key: String, action: (Int, ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: return

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        val objectArray = jsonArray.asArrayOfObject()
        objectArray.forEachIndexed { index, item -> action(index, item) }
    }

    inline fun <reified T : Any> array(key: String): Array<T> {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        return jsonArray.asArray<T>()
    }

    inline fun <reified T : Any> array(key: String, action: (T) -> Unit) {
        array<T>(key) { _, value -> action(value) }
    }

    inline fun <reified T : Any> array(key: String, action: (Int, T) -> Unit) {
        val array = array<T>(key)
        array.forEachIndexed { index, item -> action(index, item) }
    }

    @JvmName("arrayOfObject")
    fun array(key: String, action: (ValueAccess) -> Unit) {
        array(key) { _, access -> action(access) }
    }

    @JvmName("arrayOfObject")
    fun array(key: String, action: (Int, ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        jsonArray.asArrayOfObject().forEachIndexed { index, item -> action(index, item) }
    }

    inline fun <reified T : Any> mapOrNull(key: String): Map<String, T>? {
        val currentNode = findNode(node, basePath, key) ?: return null

        if (currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is an array")

        return currentNode.asMap()
    }

    inline fun <reified T : Any> mapOrNull(key: String, action: (String, T) -> Unit) {
        val map = mapOrNull<T>(key) ?: return
        map.forEach { (key, value) -> action(key, value) }
    }

    @JvmName("mapOfObjectOrNull")
    fun mapOrNull(key: String, action: (String, ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: return

        if (currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is an array")

        currentNode.asMapOfObject().forEach { (key, value) -> action(key, value) }
    }

    inline fun <reified T : Any> map(key: String): Map<String, T> {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is an array")

        return currentNode.asMap()
    }

    inline fun <reified T : Any> map(key: String, action: (String, T) -> Unit) {
        val map = map<T>(key)
        map.forEach { (key, value) -> action(key, value) }
    }

    @JvmName("mapOfObject")
    fun map(key: String, action: (String, ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is an array")

        currentNode.asMapOfObject().forEach { (key, value) -> action(key, value) }
    }

    fun exists(key: String): Boolean = findNode(node, basePath, key) != null

    fun exists(key: String, action: () -> Unit) {
        if (exists(key))
            action()
    }

}