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
 * Represents a storage size value in bytes with support for binary units (KiB, MiB, GiB).
 * Provides conversion between different storage units and arithmetic operations.
 *
 * Transformation to YAML-friendly string is roughed out to the nearest unit.
 * For example, 1024 bytes are represented as "1Ki", 1048576 bytes as "1Mi", and 1073741824 bytes as "1Gi".
 *
 * @property value The storage size in bytes
 */
@NoArgs
data class MemoryValue(val value: Long) : Comparable<MemoryValue> {
    companion object {
        /**
         * Represents zero bytes of storage
         */
        val Zero = MemoryValue(0)

        /**
         * Represents one byte of storage
         */
        val OneByte = MemoryValue(StorageUnit.BYTES.factor)

        /**
         * Represents one kibibyte (1024 bytes) of storage
         */
        val OneKiByte = MemoryValue(StorageUnit.KIBIBYTES.factor)

        /**
         * Represents one mebibyte (1024 * 1024 bytes) of storage
         */
        val OneMiByte = MemoryValue(StorageUnit.MEBIBYTES.factor)

        /**
         * Represents one gibibyte (1024 * 1024 * 1024 bytes) of storage
         */
        val OneGiByte = MemoryValue(StorageUnit.GIBIBYTES.factor)

        /**
         * Parses a string value into a StorageValue.
         * Supports values with suffixes: Ki (kibibytes), Mi (mebibytes), Gi (gibibytes).
         * If no suffix is provided, the value is interpreted as bytes.
         *
         * @param value The string representation of the storage value (e.g., "10Ki", "5Mi", "100")
         * @return The parsed StorageValue
         */
        @JsonCreator
        fun parse(value: String): MemoryValue {
            for (storageUnit in StorageUnit.entries.reversed()) {
                if (storageUnit.suffix.isNotEmpty() && value.endsWith(storageUnit.suffix, true)) {
                    return MemoryValue(value.substring(0, value.length - storageUnit.suffix.length).toInt() * storageUnit.factor)
                }
            }

            return MemoryValue(value.toLong())
        }

    }

    /**
     * Converts the storage value to a YAML-friendly string representation.
     * Automatically selects the most appropriate unit (Gi, Mi, Ki, or bytes) based on the value size.
     *
     * @return The string representation suitable for YAML serialization
     */
    @JsonValue
    fun toYamlValue(): String {
        for (storageUnit in StorageUnit.entries) {
            if (value / storageUnit.factor < 1024) {
                return "${value / storageUnit.factor}${storageUnit.suffix}"
            }
        }
        return "$value"
    }

    /**
     * Adds two storage values together.
     *
     * @param other The storage value to add
     * @return The sum of both storage values
     */
    operator fun plus(other: MemoryValue) = MemoryValue(value + other.value)

    /**
     * Subtracts one storage value from another.
     *
     * @param other The storage value to subtract
     * @return The difference between the storage values
     */
    operator fun minus(other: MemoryValue) = MemoryValue(value - other.value)

    /**
     * Multiplies the storage value by an integer factor.
     *
     * @param other The multiplication factor
     * @return The multiplied storage value
     */
    operator fun times(other: Int) = MemoryValue(value * other)

    /**
     * Divides the storage value by an integer divisor.
     *
     * @param other The division factor
     * @return The divided storage value
     */
    operator fun div(other: Int) = MemoryValue(value / other)

    /**
     * Multiplies two storage values together.
     *
     * @param other The storage value to multiply with
     * @return The product of both storage values
     */
    operator fun times(other: MemoryValue) = MemoryValue(value * other.value)

    /**
     * Divides one storage value by another.
     *
     * @param other The storage value to divide by
     * @return The quotient of the storage values
     */
    operator fun div(other: MemoryValue) = MemoryValue(value / other.value)

    /**
     * Compares this `MemoryValue` instance with the specified `MemoryValue` instance for order.
     *
     * @param other the `MemoryValue` instance to be compared with this instance.
     * @return a negative integer, zero, or a positive integer as this instance is less than,
     * equal to, or greater than the specified `MemoryValue` instance.
     */
    override fun compareTo(other: MemoryValue): Int = value.compareTo(other.value)

    /**
     * Defines the available storage units with their conversion factors and string suffixes.
     *
     * @property factor The multiplication factor to convert to bytes
     * @property suffix The string suffix used in YAML representations
     */
    enum class StorageUnit(val factor: Long, val suffix: String) {
        /**
         * Represents the smallest storage unit in terms of bytes.
         *
         * This unit serves as the base for all other storage units, with a factor of 1
         * indicating that no conversion is needed to represent the value in bytes.
         */
        BYTES(1, ""),

        /**
         * Represents the storage unit of kibibytes (KiB) within the system.
         *
         * This unit is defined as 1024 bytes and is widely used in contexts
         * where binary multiples are preferred for expressing sizes or storage capacities.
         */
        KIBIBYTES(1024, "Ki"),

        /**
         * Represents the storage unit of mebibytes (MiB) within the system.
         *
         * This unit is defined as 1,048,576 bytes (1024 * 1024) and is commonly used
         * in computing contexts to describe storage or memory capacities in binary multiples.
         */
        MEBIBYTES(1024 * 1024, "Mi"),

        /**
         * Represents the gibibyte (GiB) storage unit, which is equal to 2^30 (1,073,741,824) bytes.
         *
         * This unit is often used to measure digital storage capacity in a binary format,
         * particularly in scenarios where the distinction between binary and decimal measurement is important.
         */
        GIBIBYTES(1024 * 1024 * 1024, "Gi")
    }
}

/**
 * Convenience constant for zero bytes of storage
 */
val ZeroBytes = MemoryValue.Zero

/**
 * Convenience constant for one byte of storage
 */
val OneByte = MemoryValue.OneByte

/**
 * Convenience constant for one kibibyte of storage
 */
val OneKiByte = MemoryValue.OneKiByte

/**
 * Convenience constant for one mebibyte of storage
 */
val OneMiByte = MemoryValue.OneMiByte

/**
 * Convenience constant for one gibibyte of storage
 */
val OneGiByte = MemoryValue.OneGiByte

/**
 * Extension property to convert an integer to a storage value in bytes
 */
val Int.bytes get() = OneByte * this

/**
 * Extension property to convert an integer to a storage value in kibibytes
 */
val Int.kiBytes get() = OneKiByte * this

/**
 * Extension property to convert an integer to a storage value in mebibytes
 */
val Int.miBytes get() = OneMiByte * this

/**
 * Extension property to convert an integer to a storage value in gibibytes
 */
val Int.giBytes get() = OneGiByte * this


