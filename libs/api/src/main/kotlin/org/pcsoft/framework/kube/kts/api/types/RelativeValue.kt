/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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
 * Represents a value that can be interpreted either relatively (as a percentage) or absolutely
 * (as a fixed numeric value). This interface is designed to allow for flexible value representations
 * that depend on the context of usage.
 *
 * @param T The type of the internal value represented by the implementing class.
 * @param R The type of the value after it is transformed into a corresponding YAML-safe format.
 */
interface RelativeValue<T, R> {
    companion object {
        /**
         * Creates a PercentageValue instance representing the given floating-point percentage value.
         *
         * @param value The percentage value to be wrapped, where 1.0 represents 100%.
         * @return A PercentageValue instance containing the specified percentage value.
         */
        fun ofPercentage(value: Float): PercentageValue = PercentageValue(value)

        /**
         * Creates an AbsoluteValue instance representing the given integer value.
         *
         * @param value The absolute value to be wrapped.
         * @return An AbsoluteValue instance containing the specified value.
         */
        fun ofAbsolute(value: Int): AbsoluteValue = AbsoluteValue(value)

        @JsonCreator
        fun of(value: Any): RelativeValue<*,*> {
            return when (value) {
                is Number -> ofAbsolute(value.toInt())
                is String -> {
                    require(value.endsWith("%")) { "Invalid percentage value: $value" }
                    ofPercentage(value.dropLast(1).toFloat())
                }
                else -> throw IllegalArgumentException("Unsupported type for RelativeValue creation: ${value::class.simpleName}")
            }
        }

        /**
         * Converts the number into a PercentageValue representation.
         *
         * This property interprets the numeric value as a percentage, where 1.0 corresponds to 100%.
         * The resulting PercentageValue can be used in relative value computations or serialized
         * into formats such as YAML for configuration purposes.
         *
         * Example usage:
         * ```kotlin
         * val percentValue = 75.percent  // Represents 75%
         * println(percentValue.toYamlValue()) // Output: "75%"
         * ```
         *
         * @return A PercentageValue instance that represents this number as a percentage.
         */
        val Number.percent: PercentageValue get() = ofPercentage(this.toFloat() / 100f)

        /**
         * Computes the absolute value of this numeric instance.
         *
         * This property converts the current number into its absolute representation,
         * encapsulated in an [AbsoluteValue] object. The absolute value is the non-negative
         * magnitude of a number, regardless of its original sign.
         *
         * Example usage:
         * ```kotlin
         * val absValue = 42.absolute  // Represents absolute value 42
         * println(absValue.toYamlValue()) // Output: 42
         * ```
         *
         * @return An instance of [AbsoluteValue] representing the absolute value of this number.
         */
        val Number.absolute: AbsoluteValue get() = ofAbsolute(this.toInt())
    }

    /**
     * Represents the encapsulated value of type [T].
     * This is the core abstraction for various implementations of relative values,
     * such as percentage-based or absolute-based representations.
     */
    val value: T

    /**
     * Converts the value of this instance into its corresponding YAML representation.
     *
     * @return A value of type `R` that represents the current instance in YAML format.
     */
    @JsonValue
    fun toYamlValue(): R
}

/**
 * Represents a value interpreted as a percentage.
 *
 * This class provides a way to handle percentage-based values, where the internal representation
 * is as a floating-point number and can be converted into a percentage string format (e.g., "50%").
 *
 * @constructor Creates a new instance of PercentageValue with the specified floating-point value.
 * @param value The percentage value represented as a floating-point number, where 1.0 corresponds to 100%.
 *
 * This class implements the [RelativeValue] interface, enabling integration with frameworks or tools
 * that support relative and absolute value representations.
 */
@NoArgs
data class PercentageValue(override val value: Float) : RelativeValue<Float, String> {

    /**
     * Validates that the percentage value is within the range of 0.0f to 1.0f.
     */
    init {
        require(value in 0f..1f) { "Percentage value must be between 0f and 1f" }
    }

    /**
     * Converts the percentage value represented by this instance into its YAML-compatible string format.
     *
     * The value is internally multiplied by 100, converted to an integer, and appended with a '%' sign to
     * produce a string representation, where a floating-point value of 1.0 is interpreted as "100%".
     *
     * @return The YAML string representation of the percentage value, suffixed with a '%' sign.
     */
    override fun toYamlValue(): String = (value * 100f).toInt().toString() + "%"
}

/**
 * Represents an absolute value as an integer.
 *
 * This class encapsulates a fixed numeric value, typically used in contexts requiring
 * direct or non-relative value representations. The value can be converted into a YAML-compatible
 * format using the [toYamlValue] method.
 *
 * @constructor Creates a new instance of AbsoluteValue with the specified integer value.
 * @param value The fixed numeric value to be encapsulated.
 *
 * This class implements the [RelativeValue] interface, enabling integration with frameworks
 * or tools that support relative and absolute value representations.
 */
@NoArgs
data class AbsoluteValue(override val value: Int) : RelativeValue<Int, Int> {

    /**
     * Validates that the absolute value is non-negative.
     */
    init {
        require(value >= 0) { "Absolute value must be non-negative" }
    }

    /**
     * Converts the internal absolute value into a YAML-compatible integer representation.
     *
     * This method is used to provide a serialized format of the absolute value that is compatible
     * with YAML configurations or documents.
     *
     * @return An integer representing the current absolute value in YAML format.
     */
    override fun toYamlValue(): Int = value
}