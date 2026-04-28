package org.pcsoft.framework.kube.kts.api.values

import tools.jackson.databind.JsonNode

@Suppress("UNCHECKED_CAST")
class ValueAccess(val node: JsonNode) {

    companion object {
        fun findNode(node: JsonNode, key: String): JsonNode? {
            val pathSegments = "values.$key".split('.')

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

        inline fun <reified T> JsonNode.asMap(): Map<String, T> =
            propertyNames().associateWith { this[it].asValue<T>() }
    }

    inline fun <reified T : Any> valueOrNull(key: String): T? {
        val currentNode = findNode(node, key) ?: return null

        if (!currentNode.isValueNode)
            throw IllegalArgumentException("Key '$key' is not a value node")

        return currentNode.asValue<T>()
    }

    inline fun <reified T : Any> value(key: String): T {
        val currentNode = findNode(node, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (!currentNode.isValueNode)
            throw IllegalArgumentException("Key '$key' is not a value node")

        return currentNode.asValue<T>()
    }

    inline fun <reified T : Any> arrayOrNull(key: String): Array<T>? {
        val currentNode = findNode(node, key) ?: return null

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        return jsonArray.asArray<T>()
    }

    inline fun <reified T : Any> array(key: String): Array<T> {
        val currentNode = findNode(node, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        return jsonArray.asArray<T>()
    }

    inline fun <reified T : Any> mapOrNull(key: String): Map<String, T>? {
        val currentNode = findNode(node, key) ?: return null

        if (currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is an array")

        return currentNode.asMap()
    }

    inline fun <reified T : Any> map(key: String): Map<String, T> {
        val currentNode = findNode(node, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is an array")

        return currentNode.asMap()
    }

    fun exists(key: String): Boolean = findNode(node, key) != null

}