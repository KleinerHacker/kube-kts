package org.pcsoft.framework.kube.kts.api.values

import tools.jackson.databind.JsonNode

@Suppress("UNCHECKED_CAST")
class ValueAccess(val node: JsonNode) {

    companion object {
        fun findNode(node: JsonNode, key: String): JsonNode? {
            val pathSegments = key.split('.')

            var currentNode: JsonNode? = node
            for (segment in pathSegments) {
                currentNode = currentNode?.get(segment)
                if (currentNode == null || currentNode.isNull) {
                    return null
                }
            }
            return currentNode
        }
    }

    inline fun <reified T : Any> value(key: String, optional: Boolean = false): T? {
        val currentNode = findNode(node, key)
            ?: if (optional) return null else throw IllegalArgumentException("Key '$key' not found")

        if (!currentNode.isValueNode)
            throw IllegalArgumentException("Key '$key' is not a value node")

        return when (T::class) {
            String::class -> currentNode.asString() as? T
                ?: throw IllegalArgumentException("Key '$key' is not a string")

            Int::class -> currentNode.asInt() as? T
                ?: throw IllegalArgumentException("Key '$key' is not an integer")

            Long::class -> currentNode.asLong() as? T
                ?: throw IllegalArgumentException("Key '$key' is not a long")

            Double::class -> currentNode.asDouble() as? T
                ?: throw IllegalArgumentException("Key '$key' is not a double")

            Short::class -> currentNode.asInt().toShort() as? T
                ?: throw IllegalArgumentException("Key '$key' is not a short")

            Float::class -> currentNode.asDouble().toFloat() as? T
                ?: throw IllegalArgumentException("Key '$key' is not a float")

            Byte::class -> currentNode.asInt().toByte() as? T
                ?: throw IllegalArgumentException("Key '$key' is not a byte")

            Boolean::class -> currentNode.asBoolean() as? T
                ?: throw IllegalArgumentException("Key '$key' is not a boolean")

            else -> throw IllegalArgumentException("Unsupported type ${T::class.qualifiedName}")
        }
    }

    inline fun <reified T : Any> array(key: String, optional: Boolean = false): Array<T>? {
        val currentNode = findNode(node, key)
            ?: if (optional) return null else throw IllegalArgumentException("Key '$key' not found")

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        return Array(jsonArray.size()) {
            when (T::class) {
                String::class -> jsonArray[it].asString() as T
                Int::class -> jsonArray[it].asInt() as T
                Long::class -> jsonArray[it].asLong() as T
                Double::class -> jsonArray[it].asDouble() as T
                Short::class -> jsonArray[it].asInt().toShort() as T
                Float::class -> jsonArray[it].asDouble().toFloat() as T
                Byte::class -> jsonArray[it].asInt().toByte() as T
                Boolean::class -> jsonArray[it].asBoolean() as T
                else -> throw IllegalArgumentException("Unsupported type ${T::class.qualifiedName}")
            }
        }
    }

}