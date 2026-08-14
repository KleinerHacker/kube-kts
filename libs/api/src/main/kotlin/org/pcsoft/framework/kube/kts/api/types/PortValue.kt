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

package org.pcsoft.framework.kube.kts.api.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents a Kubernetes port reference that is either a numeric port or the name of a named port.
 *
 * Kubernetes models such fields as `IntOrString`: they accept a plain integer (the port number) or a
 * string (the `name` of a port declared on a container). This interface abstracts over both forms so
 * that the rendered YAML carries the correct scalar type in each case.
 *
 * @param R The scalar type this value is rendered as in YAML - [Int] for numeric ports, [String] for named ports.
 *
 * @see NumberPortValue
 * @see NamePortValue
 */
interface PortValue<R> {
    companion object {
        /**
         * Creates a [PortValue] from an arbitrary scalar read from YAML or JSON.
         *
         * Numeric input produces a [NumberPortValue], textual input a [NamePortValue].
         *
         * @param value The scalar value to convert. Must be a [Number] or a [String].
         * @return The matching [PortValue] implementation.
         * @throws IllegalArgumentException If the value is neither a [Number] nor a [String].
         */
        @JsonCreator
        @JvmStatic
        fun of(value: Any): PortValue<*> = when (value) {
            is Number -> ofPortNumber(value.toInt())
            is String -> ofPortName(value)
            else -> throw IllegalArgumentException(
                "Unsupported type for PortValue creation: ${value::class.simpleName}"
            )
        }
    }

    /**
     * Converts this port reference into the scalar value written to YAML.
     *
     * @return The numeric port for [NumberPortValue], the port name for [NamePortValue].
     */
    @JsonValue
    fun toYamlValue(): R
}

/**
 * A port referenced by its number.
 *
 * @constructor Creates a new numeric port reference.
 * @property value The port number. Must be within the valid TCP/UDP port range of 1 to 65535.
 */
@NoArgs
data class NumberPortValue(val value: Int) : PortValue<Int>, Comparable<NumberPortValue> {

    /**
     * Validates that the port number lies within the valid port range.
     */
    init {
        require(value in 1..65535) { "Port number must be between 1 and 65535, but was $value" }
    }

    /**
     * Converts this reference into its YAML representation.
     *
     * @return The port number as an integer.
     */
    override fun toYamlValue(): Int = value

    /**
     * Compares this port number with another one.
     *
     * @param other The port number to compare against.
     * @return A negative integer, zero, or a positive integer as this port is less than, equal to,
     *         or greater than the other port.
     */
    override fun compareTo(other: NumberPortValue): Int = value.compareTo(other.value)
}

/**
 * A port referenced by the name of a container port.
 *
 * The name must match the `name` of a port declared on a container of the targeted Pod. Kubernetes
 * requires it to be a valid IANA service name: at most 15 characters, lower-case alphanumerics and
 * `-`, containing at least one letter and no adjacent or leading/trailing hyphens.
 *
 * @constructor Creates a new named port reference.
 * @property value The name of the referenced container port.
 */
@NoArgs
data class NamePortValue(val value: String) : PortValue<String> {

    /**
     * Validates that the port name is a syntactically valid IANA service name.
     */
    init {
        require(value.isNotBlank()) { "Port name must not be blank" }
        require(value.length <= 15) { "Port name must not exceed 15 characters, but was '$value'" }
        require(value.matches(NAME_PATTERN)) {
            "Port name must consist of lower-case alphanumeric characters and '-' only, " +
                    "must not start or end with '-' and must not contain consecutive '-', but was '$value'"
        }
        require(value.any { it.isLetter() }) { "Port name must contain at least one letter, but was '$value'" }
    }

    /**
     * Converts this reference into its YAML representation.
     *
     * @return The port name as a string.
     */
    override fun toYamlValue(): String = value

    private companion object {
        private val NAME_PATTERN = Regex("^[a-z0-9]+(-[a-z0-9]+)*$")
    }
}

/**
 * Creates a numeric port reference.
 *
 * Example usage:
 * ```kotlin
 * targetPort = ofPortNumber(8080)
 * ```
 *
 * @param value The port number. Must be between 1 and 65535.
 * @return A [NumberPortValue] wrapping the given port number.
 */
fun ofPortNumber(value: Int): NumberPortValue = NumberPortValue(value)

/**
 * Creates a named port reference.
 *
 * Example usage:
 * ```kotlin
 * targetPort = ofPortName("http")
 * ```
 *
 * @param value The name of the referenced container port.
 * @return A [NamePortValue] wrapping the given port name.
 */
fun ofPortName(value: String): NamePortValue = NamePortValue(value)

/**
 * Interprets this number as a numeric port reference.
 *
 * Example usage:
 * ```kotlin
 * targetPort = 8080.portNumber
 * ```
 *
 * @return A [NumberPortValue] representing this number.
 */
val Number.portNumber: NumberPortValue get() = ofPortNumber(this.toInt())

/**
 * Interprets this string as a named port reference.
 *
 * Example usage:
 * ```kotlin
 * targetPort = "http".portName
 * ```
 *
 * @return A [NamePortValue] representing this string.
 */
val String.portName: NamePortValue get() = ofPortName(this)
