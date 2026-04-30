package org.pcsoft.framework.kube.kts.api.values

import tools.jackson.databind.JsonNode

/**
 * The `ValueAccess` class provides utility functions to encapsulate access to nodes 
 * within a JSON structure. It supports typed retrieval of values, iteration over 
 * arrays and maps, and traversal of nested objects.
 *
 * This class is designed to work with JSON structures and exposes a flexible API 
 * for accessing and manipulating data in a type-safe manner.
 *
 * @param node The JSON node instance that represents the underlying JSON structure.
 * @param basePath Optional base path used for key resolution in nested JSON structures.
 */
@Suppress("UNCHECKED_CAST")
class ValueAccess private constructor(val node: JsonNode, val basePath: String?) {

    companion object {
        /**
         * Creates a new instance of [ValueAccess] initialized with the given JSON node and a base path of "values".
         *
         * @param node The root JSON node to use as the data source for value access.
         * @return A [ValueAccess] instance configured with the specified root node and base path.
         */
        fun ofRoot(node: JsonNode): ValueAccess = ValueAccess(node, "values")

        /**
         * Creates a new instance of ValueAccess scoped to the specified child node.
         *
         * @param node The JSON node to initialize the new ValueAccess instance with.
         * @return A new ValueAccess instance wrapping the provided JSON node.
         */
        fun ofChild(node: JsonNode): ValueAccess = ValueAccess(node, null)

        /**
         * Finds a nested JSON node based on a given key or path.
         *
         * If a `basePath` is specified, it will be prepended to the `key` before resolving the path.
         * The path segments are split by `.` and traversed recursively.
         *
         * @param node The root JSON node from which the search starts.
         * @param basePath An optional base path to prepend to the key. May be null.
         * @param key The key or path (dot-separated) to the target node.
         * @return The found `JsonNode` if the key or path exists, or `null` if the node cannot be found.
         */
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

        /**
         * Converts the current `JsonNode` instance into a value of the specified type.
         * This method infers the desired type `T` and attempts to convert the node's value
         * accordingly. If the conversion is not possible or the type is unsupported, an
         * `IllegalArgumentException` is thrown.
         *
         * Supported types include:
         * - `String`
         * - `Int`
         * - `Long`
         * - `Double`
         * - `Short`
         * - `Float`
         * - `Byte`
         * - `Boolean`
         * 
         * @return The value of the `JsonNode` converted to the specified type `T`.
         * @throws IllegalArgumentException If the type `T` is unsupported or the value cannot be converted.
         */
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

        /**
         * Converts the current `JsonNode` to an array of the specified type `T`.
         * 
         * This method processes the elements of the `JsonNode` assuming it represents an array
         * and converts each child node into the type `T` using `asValue`. If the current node is not an array
         * or if any conversion fails, an exception is thrown.
         *
         * @return An array of type `T` containing the converted values of the child nodes.
         * @throws IllegalArgumentException If the conversion to type `T` is unsupported or if any node cannot be converted.
         */
        inline fun <reified T> JsonNode.asArray(): Array<T> =
            (0 until size()).map { i -> this[i].asValue<T>() }.toTypedArray()

        /**
         * Converts the current `JsonNode` into an array of `ValueAccess` instances.
         * Each child node within the JSON array is wrapped into a `ValueAccess` instance.
         * 
         * @return An array of `ValueAccess` objects, each representing a child of the JSON node.
         */
        fun JsonNode.asArrayOfObject(): Array<ValueAccess> =
            (0 until size()).map { i -> ofChild(this[i]) }.toTypedArray()

        /**
         * Converts the current `JsonNode` into a `Map` where the keys are the property names of the node
         * and the values are converted to the specified type `T`.
         *
         * This function iterates over the property names of the `JsonNode` and attempts to retrieve
         * and convert each property's value to the given type `T`.
         *
         * @return A `Map` with the property names as keys and their corresponding values of type `T`.
         * @throws IllegalArgumentException If the conversion to the specified type `T` fails or is unsupported.
         */
        inline fun <reified T> JsonNode.asMap(): Map<String, T> =
            propertyNames().associateWith { this[it].asValue<T>() }

        /**
         * Converts the current `JsonNode` into a map where the keys are the property names of the node
         * and the values are `ValueAccess` objects representing the corresponding child nodes.
         *
         * @return A map containing the property names as keys and their corresponding `ValueAccess` instances as values.
         */
        fun JsonNode.asMapOfObject(): Map<String, ValueAccess> =
            propertyNames().associateWith { ofChild(this[it]) }
    }

    /**
     * Attempts to retrieve the value associated with the specified key and convert it to the desired type.
     * If the key does not exist or is not a value node, returns null instead of throwing an exception.
     *
     * Example:
     * ```kotlin
     * val timeout = valueOrNull<Int>("spec.timeout") ?: 30
     * ```
     *
     * @param key The key or path (dot-separated) of the value to retrieve.
     * @return The value of the specified type `T` if found, or null if the key does not exist or is not a value node.
     * @throws IllegalArgumentException If the node associated with the key is not a value node.
     */
    inline fun <reified T : Any> valueOrNull(key: String): T? {
        val currentNode = findNode(node, basePath, key) ?: return null

        if (!currentNode.isValueNode)
            throw IllegalArgumentException("Key '$key' is not a value node")

        return currentNode.asValue<T>()
    }

    /**
     * Executes an action on a nested `ValueAccess` instance if the specified key exists.
     *
     * The method attempts to locate a node specified by the key, relative to the current context. 
     * If the node is found, the provided `action` is executed with a `ValueAccess` instance
     * wrapping the found node. If the key does not resolve to a valid node, the method returns 
     * without invoking the action.
     *
     * Example:
     * ```kotlin
     * valueOrNull("spec.resources") {
     *     val cpu = it.value<String>("limits.cpu")
     * }
     * ```
     *
     * @param key The key or path (dot-separated) to locate the target node.
     * @param action A function to execute, receiving a `ValueAccess` instance scoped to the found node.
     */
    fun valueOrNull(key: String, action: (ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: return
        action(ValueAccess(currentNode, basePath))
    }

    /**
     * Retrieves a single value of a specific type.
     * Throws an exception if the key is missing or not a value node.
     *
     * Example:
     * ```kotlin
     * val replicas = value<Int>("spec.replicas")
     * ```
     */
    inline fun <reified T : Any> value(key: String): T {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (!currentNode.isValueNode)
            throw IllegalArgumentException("Key '$key' is not a value node")

        return currentNode.asValue<T>()
    }

    /**
     * Executes an action on a nested [ValueAccess] scope if the key exists.
     *
     * Example:
     * ```kotlin
     * value("spec.resources") {
     *     val cpu = it.value<String>("limits.cpu")
     * }
     * ```
     */
    fun value(key: String, action: (ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")
        action(ValueAccess(currentNode, basePath))
    }

    /**
     * Attempts to retrieve the value associated with the specified key and convert it to an array of the desired type.
     * Returns null if the key does not exist or is not an array node.
     *
     * Example:
     * ```kotlin
     * val tags = arrayOrNull<String>("metadata.tags") ?: emptyArray()
     * ```
     *
     * @param key The key or path (dot-separated) to locate the target node.
     * @return An array of type `T` containing the values of the array node, or null if the key does not exist 
     * or is not an array node.
     * @throws IllegalArgumentException If the node associated with the key is not an array node.
     */
    inline fun <reified T : Any> arrayOrNull(key: String): Array<T>? {
        val currentNode = findNode(node, basePath, key) ?: return null

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        return jsonArray.asArray<T>()
    }

    /**
     * Iterates over an array of values of a specific type, providing both the index and the value to the action.
     *
     * This method attempts to retrieve the array associated with the given key and processes its elements.
     * If the key does not exist or is not an array node, the function returns without invoking the action.
     *
     * Example:
     * ```kotlin
     * arrayOrNull<String>("metadata.tags") { tag ->
     *     println("Tag: $tag")
     * }
     * ```
     *
     * @param key The key or path (dot-separated) to locate the target array node.
     * @param action A lambda function that receives the index and the value of each array element as parameters.
     */
    inline fun <reified T : Any> arrayOrNull(key: String, action: (T) -> Unit) {
        arrayOrNull<T>(key) { _, value -> action(value) }
    }

    /**
     * Iterates over the elements of an array associated with the specified key, providing both the index and value 
     * of each element to the given action.
     *
     * Attempts to retrieve the array of the specified type `T` corresponding to the key. If the key does not exist 
     * or the value is not an array node, the function returns without invoking the action.
     *
     * Example:
     * ```kotlin
     * arrayOrNull<String>("metadata.tags") { index, tag ->
     *     println("Tag $index: $tag")
     * }
     * ```
     *
     * @param key The key or path (dot-separated) to locate the target array node.
     * @param action A lambda function that receives the index and value of each element in the array as parameters.
     *               The index starts at 0 and increments for each element.
     * @throws IllegalArgumentException If the node associated with the key is not an array node.
     */
    inline fun <reified T : Any> arrayOrNull(key: String, action: (Int, T) -> Unit) {
        val array = arrayOrNull<T>(key) ?: return
        array.forEachIndexed { index, item -> action(index, item) }
    }

    /**
     * Iterates over an array of objects associated with the specified key, providing both the index 
     * and a [ValueAccess] instance for each element.
     *
     * This method attempts to locate the array node specified by the key, relative to the current 
     * context. If the node exists and is an array, the given `action` is invoked for each element 
     * in the array. If the key does not resolve to a valid array node, the function returns 
     * without invoking the action.
     *
     * Example:
     * ```kotlin
     * arrayOrNull("spec.containers") {
     *     val name = it.value<String>("name")
     *     println("Container: $name")
     * }
     * ```
     *
     * @param key The key or path (dot-separated) to locate the target array node.
     * @param action A lambda function that receives the index and a [ValueAccess] instance scoped 
     *               to each array element. The index starts at 0 and increments for each element.
     * @throws IllegalArgumentException If the node associated with the key is not an array.
     */
    @JvmName("arrayOfObjectOrNull")
    fun arrayOrNull(key: String, action: (ValueAccess) -> Unit) {
        arrayOrNull(key) { _, access -> action(access) }
    }

    /**
     * Iterates over an array of objects associated with the specified key, providing the index and a [ValueAccess] 
     * instance for each element of the array.
     *
     * This method attempts to locate the array node specified by the key. If the node exists and is an array, the 
     * provided `action` lambda is invoked for each element of the array, supplying the index and a [ValueAccess] 
     * instance for scoped access to the element. If the key does not resolve to a valid array node, the method 
     * returns without invoking the action.
     *
     * Example:
     * ```kotlin
     * arrayOrNull("spec.containers") { index, container ->
     *     val name = container.value<String>("name")
     *     println("Container $index: $name")
     * }
     * ```
     *
     * @param key The key or path (dot-separated) to locate the target array node.
     * @param action A lambda function that receives the index of each element and a [ValueAccess] instance
     *               scoped to the element. The index starts at 0 and increments for each element.
     * @throws IllegalArgumentException If the node associated with the key is not an array.
     */
    @JvmName("arrayOfObjectOrNull")
    fun arrayOrNull(key: String, action: (Int, ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: return

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        val objectArray = jsonArray.asArrayOfObject()
        objectArray.forEachIndexed { index, item -> action(index, item) }
    }

    /**
     * Retrieves an array of elements of type [T] from a JSON-like structure based on the provided key.
     *
     * Example:
     * ```kotlin
     * val tags = array<String>("metadata.tags")
     * ```
     *
     * @param key The key used to locate the array within the current structure.
     * @return An array of elements of type [T] corresponding to the specified key.
     * @throws IllegalArgumentException If the key is not found or if the value associated with the key is not an array.
     */
    inline fun <reified T : Any> array(key: String): Array<T> {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        return jsonArray.asArray<T>()
    }

    /**
     * Iterates over an array of values of a specific type.
     *
     * Example:
     * ```kotlin
     * array<String>("metadata.tags") { tag ->
     *     println("Tag: $tag")
     * }
     * ```
     */
    inline fun <reified T : Any> array(key: String, action: (T) -> Unit) {
        array<T>(key) { _, value -> action(value) }
    }

    /**
     * Iterates over each element in an array obtained using the specified key and performs the given action
     * on each element along with its index.
     *
     * Example:
     * ```kotlin
     * array<String>("metadata.tags") { index, tag ->
     *     println("Tag $index: $tag")
     * }
     * ```
     *
     * @param T The type of elements in the array.
     * @param key The key used to retrieve the array.
     * @param action A lambda function to be invoked for each element in the array, 
     *               receiving the index of the element and the element itself as parameters.
     */
    inline fun <reified T : Any> array(key: String, action: (Int, T) -> Unit) {
        val array = array<T>(key)
        array.forEachIndexed { index, item -> action(index, item) }
    }

    /**
     * Creates an array in the provided context using the specified key and action.
     *
     * Example:
     * ```kotlin
     * array("spec.containers") { container ->
     *     val name = container.value<String>("name")
     *     println("Container: $name")
     * }
     * ```
     *
     * @param key The key used to identify the array.
     * @param action A lambda function that takes a [ValueAccess] parameter 
     * and defines the behavior to apply within the array context.
     */
    @JvmName("arrayOfObject")
    fun array(key: String, action: (ValueAccess) -> Unit) {
        array(key) { _, access -> action(access) }
    }

    /**
     * Iterates over the elements of a JSON array associated with the given key and performs the specified action on each.
     *
     * Example:
     * ```kotlin
     * array("spec.containers") { index, container ->
     *     val name = container.value<String>("name")
     *     println("Container $index: $name")
     * }
     * ```
     *
     * @param key the key identifying the JSON array to iterate over
     * @param action a lambda function to be invoked for each element in the array, providing the element's index and a [ValueAccess] instance for accessing the element's value
     * @throws IllegalArgumentException if the key is not found or the associated value is not an array
     */
    @JvmName("arrayOfObject")
    fun array(key: String, action: (Int, ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (!currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is not an array")

        val jsonArray = currentNode.asArray()

        jsonArray.asArrayOfObject().forEachIndexed { index, item -> action(index, item) }
    }

    /**
     * Attempts to find a node corresponding to the given key and converts it to a map of the specified type.
     * Returns null if the key cannot be resolved or does not exist.
     *
     * Example:
     * ```kotlin
     * val labels = mapOrNull<String>("metadata.labels") ?: emptyMap()
     * ```
     *
     * @param key The key used to locate the target node.
     * @return A map of the specified type if the node is found and valid, or null if the key cannot be resolved.
     * @throws IllegalArgumentException If the resolved node is an array.
     */
    inline fun <reified T : Any> mapOrNull(key: String): Map<String, T>? {
        val currentNode = findNode(node, basePath, key) ?: return null

        if (currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is an array")

        return currentNode.asMap()
    }

    /**
     * Invokes the specified action on each entry in the map returned by the `mapOrNull` function
     * for the given key, if such a map exists. If no map is found, this function does nothing.
     *
     * Example:
     * ```kotlin
     * mapOrNull<String>("metadata.labels") { key, value ->
     *     println("Label $key: $value")
     * }
     * ```
     *
     * @param key The key used to retrieve the map.
     * @param action A lambda function to be executed on each entry of the map, which takes 
     * a key of type String and a value of type T as parameters.
     */
    inline fun <reified T : Any> mapOrNull(key: String, action: (String, T) -> Unit) {
        val map = mapOrNull<T>(key) ?: return
        map.forEach { (key, value) -> action(key, value) }
    }

    /**
     * Executes the provided action on each key-value pair from the node identified by the given key.
     * If the key does not resolve to a valid node, the function returns without performing any action.
     * Throws an exception if the identified node is an array.
     *
     * Example:
     * ```kotlin
     * mapOrNull("spec.selector") { key, selector ->
     *     val matchLabel = selector.value<String>("matchLabels.$key")
     *     println("Selector $key: $matchLabel")
     * }
     * ```
     *
     * @param key The key used to locate the node from which key-value pairs will be retrieved.
     * @param action A lambda function that will be executed for each key-value pair. 
     *        Receives the key and the corresponding value as arguments.
     */
    @JvmName("mapOfObjectOrNull")
    fun mapOrNull(key: String, action: (String, ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: return

        if (currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is an array")

        currentNode.asMapOfObject().forEach { (key, value) -> action(key, value) }
    }

    /**
     * Maps the specified key to a `Map<String, T>` by finding the corresponding node
     * in the structure and converting it to the desired map format.
     *
     * Example:
     * ```kotlin
     * val labels = map<String>("metadata.labels")
     * ```
     *
     * @param key The key to be used for locating the corresponding node.
     * @return A map of type `Map<String, T>` derived from the located node.
     * @throws IllegalArgumentException If the key does not exist, or if the corresponding node is an array.
     */
    inline fun <reified T : Any> map(key: String): Map<String, T> {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is an array")

        return currentNode.asMap()
    }

    /**
     * Applies the specified action to each entry in a map associated with the given key.
     *
     * Example:
     * ```kotlin
     * map<String>("metadata.labels") { key, value ->
     *     println("Label $key: $value")
     * }
     * ```
     *
     * @param key The key used to retrieve the map.
     * @param action A lambda function that takes a key-value pair from the map and performs an operation.
     */
    inline fun <reified T : Any> map(key: String, action: (String, T) -> Unit) {
        val map = map<T>(key)
        map.forEach { (key, value) -> action(key, value) }
    }

    /**
     * Iterates over a map-like structure represented by the specified key, applying the given action for each key-value pair.
     *
     * Example:
     * ```kotlin
     * map("spec.selector") { key, selector ->
     *     val matchLabel = selector.value<String>("matchLabels.$key")
     *     println("Selector $key: $matchLabel")
     * }
     * ```
     *
     * @param key The key used to locate the node within the base path. Must not reference an array.
     * @param action A lambda function that will be invoked for each key-value pair in the map. 
     *               The first parameter is the key (as a String), and the second parameter is a `ValueAccess` object.
     * 
     * @throws IllegalArgumentException If the key is not found or points to an array.
     */
    @JvmName("mapOfObject")
    fun map(key: String, action: (String, ValueAccess) -> Unit) {
        val currentNode = findNode(node, basePath, key) ?: throw IllegalArgumentException("Key '$key' not found")

        if (currentNode.isArray)
            throw IllegalArgumentException("Key '$key' is an array")

        currentNode.asMapOfObject().forEach { (key, value) -> action(key, value) }
    }

    /**
     * Checks if a node with the specified key exists.
     *
     * Example:
     * ```kotlin
     * if (exists("spec.replicas")) {
     *     val replicas = value<Int>("spec.replicas")
     * }
     * ```
     *
     * @param key The key to search for within the structure.
     * @return `true` if a node with the specified key exists, otherwise `false`.
     */
    fun exists(key: String): Boolean = findNode(node, basePath, key) != null

    /**
     * Checks if a given key exists and performs the specified action if it does.
     *
     * Example:
     * ```kotlin
     * exists("spec.replicas") {
     *     println(value<Int>("spec.replicas"))
     * }
     * ```
     *
     * @param key The key to check for existence.
     * @param action The action to perform if the key exists.
     */
    fun exists(key: String, action: () -> Unit) {
        if (exists(key))
            action()
    }

}