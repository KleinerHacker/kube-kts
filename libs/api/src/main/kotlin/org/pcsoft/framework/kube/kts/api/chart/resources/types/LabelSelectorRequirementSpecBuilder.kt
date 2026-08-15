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

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.chart.resources.types.LabelSelectorRequirementSpec.Operator

/**
 * A builder class for defining and constructing label selector requirements.
 *
 * A label selector requirement specifies criteria for matching resources
 * based on their labels. The builder allows you to specify a label key,
 * an operator, and an optional list of values that define the selection logic.
 *
 * This class is designed to support programs that need to configure label
 * selection logic dynamically, typically in contexts such as Kubernetes
 * resource selectors.
 *
 * @constructor Creates a new instance of the builder.
 * @param key The label key associated with the selector requirement.
 * @param operator The operator that defines how the key should be matched.
 */
class LabelSelectorRequirementSpecBuilder internal constructor(
    private var key: String,
    private var operator: Operator
) {
    private var values: MutableList<String>? = null

    /**
     * Adds a value to the internal list of values. If the list does not exist, it is initialized.
     *
     * @param value The value to be added to the list.
     */
    fun addValue(value: String) {
        if (values == null) {
            values = mutableListOf()
        }
        values!!.add(value)
    }

    /**
     * Adds multiple values to the internal list of values. If the list has not been initialized, it will
     * be created and the provided values will be added to it.
     *
     * @param values A variable number of string values to be added to the list. Each value represents an
     *               additional criterion to include in the label selector requirement.
     */
    fun addValues(vararg values: String) {
        if (this.values == null) {
            this.values = mutableListOf()
        }
        this.values!!.addAll(values)
    }

    /**
     * Configures a list of values using the provided setup block.
     *
     * This method allows customization of a `ValueListBuilder` to define
     * a collection of values. It initializes a new builder instance and
     * applies the specified configuration to it.
     *
     * @param prepare A function literal with receiver that allows configuring
     *                a `ValueListBuilder`. Use this block to define values
     *                to be included in the list.
     */
    fun values(prepare: ValueListBuilder.() -> Unit) =
        ValueListBuilder().apply(prepare)

    /**
     * Builds and returns a `LabelSelectorRequirementSpec` instance using the current state
     * of the `LabelSelectorRequirementSpecBuilder`.
     *
     * The returned object represents the configured requirements for a label selector, including
     * the key, operator, and values. This method finalizes the builder's setup into an immutable
     * `LabelSelectorRequirementSpec`.
     *
     * @return A new `LabelSelectorRequirementSpec` instance configured with the builder's state.
     */
    internal fun build(): LabelSelectorRequirementSpec =
        LabelSelectorRequirementSpec(key, operator, values)

    /**
     * A builder for constructing and managing a list of values used in a label selector requirement.
     *
     * This class provides functionality to add individual values to a collection managed internally.
     * Instances of this builder class are typically initialized internally by the containing class.
     */
    inner class ValueListBuilder internal constructor() {
        /**
         * Adds a value to the internal list of values managed by the builder.
         *
         * @param value The string value to be added.
         */
        fun value(value: String) = addValue(value)
    }
}
