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
 * Represents a CPU value, which is used to define CPU resource allocations or limits.
 * This value is internally stored as a floating-point number representing CPU units.
 *
 * @constructor Creates a new CpuValue instance with the specified CPU value.
 *
 * @property value The CPU value as a floating-point number, where `1.0` represents a full CPU core.
 */
@NoArgs
data class CpuValue(val value: Float) : Comparable<CpuValue> {
    companion object {
        /**
         * Represents a CPU value of zero, corresponding to no CPU allocation or usage.
         * This is a predefined constant for scenarios where zero CPU resources are needed.
         */
        val Zero = CpuValue(0f)

        /**
         * Represents a predefined CPU value of one full CPU core.
         * This constant is commonly used to allocate or limit a resource to exactly one CPU core.
         */
        val One = CpuValue(1f)

        /**
         * Parses a CPU value string into a CpuValue object. The input string can either represent
         * milliCPU units (e.g., "500m" for 0.5 CPU) or full CPU units (e.g., "1.0" for 1 CPU).
         *
         * @param value The CPU value string to be parsed. It supports two formats:
         *              - MilliCPU format: Ends with 'm', e.g., "500m".
         *              - Full CPU format: A floating-point number, e.g., "1.0".
         * @return A CpuValue object representing the parsed CPU value as a floating-point value.
         * @throws NumberFormatException if the input string is not a valid number format.
         */
        @JsonCreator
        fun parse(value: String): CpuValue {
            if (value.endsWith("m")) {
                return CpuValue(value.substring(0, value.length - 1).toFloat() / 1000f)
            }

            return CpuValue(value.toFloat())
        }
    }

    /**
     * Adds the value of another CpuValue instance to this CpuValue instance.
     *
     * @param other The other CpuValue instance to be added to this instance.
     * @return A new CpuValue instance representing the sum of the two values.
     */
    operator fun plus(other: CpuValue) = CpuValue(value + other.value)

    /**
     * Subtracts the value of another CpuValue instance from this CpuValue instance.
     *
     * @param other The other CpuValue instance to be subtracted from this instance.
     * @return A new CpuValue instance representing the difference between the two values.
     */
    operator fun minus(other: CpuValue) = CpuValue(value - other.value)

    /**
     * Multiplies this CpuValue instance by a scalar value of type Float.
     *
     * @param other The scalar value to multiply the CpuValue instance by.
     * @return A new CpuValue instance representing the product of the current value and the scalar.
     */
    operator fun times(other: Float) = CpuValue(value * other)

    /**
     * Divides the value of this CpuValue instance by a scalar value of type Float.
     *
     * @param other The scalar value to divide the CpuValue instance by.
     * @return A new CpuValue instance representing the result of the division.
     */
    operator fun div(other: Float) = CpuValue(value / other)

    /**
     * Multiplies the value of this CpuValue instance by the value of another CpuValue instance.
     *
     * @param other The other CpuValue instance whose value is to be multiplied with this instance's value.
     * @return A new CpuValue instance representing the product of the two values.
     */
    operator fun times(other: CpuValue) = CpuValue(value * other.value)

    /**
     * Divides the value of this CpuValue instance by the value of another CpuValue instance.
     *
     * @param other The other CpuValue instance whose value is used as the divisor.
     * @return A new CpuValue instance representing the result of the division.
     */
    operator fun div(other: CpuValue) = CpuValue(value / other.value)

    /**
     * Converts the CPU value to its YAML-compatible string representation in milliCPU units.
     * The milliCPU unit is a thousandth of a CPU core, represented as an integer followed by 'm'.
     *
     * @return The YAML-compatible string representation of the CPU value, formatted in milliCPU units (e.g., "500m").
     */
    @JsonValue
    fun toYamlValue(): String = "${(value * 1000f).toInt()}m"

    /**
     * Compares this `CpuValue` instance with the specified `CpuValue` object for order.
     *
     * @param other the `CpuValue` instance to be compared.
     * @return a negative integer, zero, or a positive integer as this instance is less than,
     *         equal to, or greater than the specified object.
     */
    override fun compareTo(other: CpuValue): Int = value.compareTo(other.value)
}

/**
 * Creates a `CpuValue` instance from the specified floating-point value.
 *
 * @param value The CPU value as a floating-point number, where `1.0` represents a full CPU core.
 *              The value must be non-negative.
 * @return A `CpuValue` object representing the specified CPU value.
 */
fun cpuOf(value: Float) = CpuValue(value)

/**
 * Extension property to convert a floating-point number into a `CpuValue` instance.
 *
 * This property allows interpreting a Float as a CPU allocation or limit, with `1.0` representing
 * a full CPU core. It simplifies the creation of `CpuValue` objects to represent resource constraints
 * for CPU usage.
 */
val Float.cpu get() = CpuValue(this)

/**
 * Extension property to convert an integer value representing CPU usage in microseconds
 * into a `CpuValue` instance.
 *
 * This property allows interpreting an Int as a CPU allocation or limit, with `1000` representing
 * a full CPU core. It simplifies the creation of `CpuValue` objects to represent resource constraints
 * for CPU usage.
 */
val Int.mCpu get() = (this.toFloat() / 1000f).cpu

/**
 * Provides a default CPU value that represents "zero" or "no CPU".
 *
 * This property is typically used in contexts where an absence or nullification
 * of CPU allocation/capacity needs to be expressed.
 */
val noCpu get() = CpuValue.Zero

/**
 * A property that provides a predefined CPU value representing a single CPU allocation.
 * It is commonly used in resource definitions to specify that only one CPU core
 * should be allocated or utilized.
 */
val oneCpu get() = CpuValue.One