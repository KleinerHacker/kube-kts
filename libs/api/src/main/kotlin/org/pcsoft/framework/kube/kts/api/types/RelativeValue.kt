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

        /**
         * Converts the current Float value into a PercentageValue instance.
         *
         * This function interprets the Float value as a percentage, where a value of 1.0 represents 100%.
         *
         * @return A PercentageValue instance wrapping the current Float value as a percentage.
         */
        fun Float.percent(): PercentageValue = ofPercentage(this)

        /**
         * Converts the current integer into an `AbsoluteValue` instance.
         *
         * This function wraps the integer into an absolute representation, enabling it
         * to be used in contexts where absolute values are required.
         *
         * @return An `AbsoluteValue` instance representing the current integer.
         */
        fun Int.absolute(): AbsoluteValue = ofAbsolute(this)
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
     * Converts the internal absolute value into a YAML-compatible integer representation.
     *
     * This method is used to provide a serialized format of the absolute value that is compatible
     * with YAML configurations or documents.
     *
     * @return An integer representing the current absolute value in YAML format.
     */
    override fun toYamlValue(): Int = value
}