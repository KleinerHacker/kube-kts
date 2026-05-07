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

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.chart.resources.types.LabelSelectorRequirementSpec.Operator

/**
 * A builder class for constructing an instance of `LabelSelectorSpec`.
 *
 * The `LabelSelectorSpecBuilder` provides methods for configuring both `matchLabels`
 * and `matchExpressions`, which are used to define the selection criteria for Kubernetes
 * resources based on their labels.
 *
 * The `matchLabels` is a key-value pair map, where the key is the label name and the value
 * is the required label value. The `matchExpressions` is a list of conditions, each defined
 * using a key, an operator, and, optionally, a set of values.
 *
 * Some values are required.
 */
class LabelSelectorSpecBuilder internal constructor() {
    private var matchLabels: MutableMap<String, String>? = null
    private var matchExpressions: MutableList<LabelSelectorRequirementSpecBuilder>? = null

    /**
     * Adds a key-value pair to the `matchLabels` map. If the map is null, it initializes it
     * as a mutable map before adding the key-value pair.
     *
     * @param key The key to be added to the matchLabels map.
     * @param value The value associated with the given key in the matchLabels map.
     */
    fun addMatchLabel(key: String, value: String) {
        if (matchLabels == null) {
            matchLabels = mutableMapOf()
        }
        matchLabels!![key] = value
    }

    /**
     * Configures a set of key-value label pairs that are used to match specific resources.
     *
     * This function initializes a `MatchLabelListBuilder`, applies the provided configuration,
     * and constructs the resulting label specification. The configuration is defined by invoking
     * the DSL-style block provided as a parameter.
     *
     * @param prepare A lambda function that defines the configuration for the `MatchLabelListBuilder`.
     *                This parameter allows specifying multiple label key-value pairs to be added to
     *                the `matchLabels` collection.
     */
    fun matchLabels(prepare: MatchLabelListBuilder.() -> Unit) =
        MatchLabelListBuilder().apply(prepare)

    /**
     * Adds a match expression to the `matchExpressions` list with the specified key, operator,
     * and an optional configuration block for additional customization.
     *
     * If the `matchExpressions` list is null, it initializes the list before adding the
     * newly constructed match expression.
     *
     * @param key The label key to be matched. This represents the label field in the label selector requirement.
     * @param operator The `Operator` that defines how the label key should be matched. It indicates
     *                 whether the key must exist, must not exist, or must have specific values.
     * @param prepare An optional lambda function used to configure the match expression's values
     *                through the `LabelSelectorRequirementSpecBuilder`.
     */
    fun addMatchExpression(key: String, operator: Operator, prepare: LabelSelectorRequirementSpecBuilder.() -> Unit = {}) {
        if (matchExpressions == null) {
            matchExpressions = mutableListOf()
        }
        matchExpressions!!.add(LabelSelectorRequirementSpecBuilder(key, operator).apply(prepare))
    }

    /**
     * Configures a list of match expressions that define selection criteria for resources.
     *
     * This method initializes a `MatchExpressionListBuilder`, applies the given configuration block,
     * and builds a list of match expressions. It is used to specify custom rules for resource selection
     * based on labels.
     *
     * @param prepare A lambda function that defines the configuration for the `MatchExpressionListBuilder`.
     *                This parameter allows specifying multiple match expressions to be added to the
     *                `matchExpressions` list.
     */
    fun matchExpressions(prepare: MatchExpressionListBuilder.() -> Unit) =
        MatchExpressionListBuilder().apply(prepare)

    /**
     * Builds a `LabelSelectorSpec` based on the current state of the builder.
     *
     * Combines the `matchLabels` map and a mapped version of the `matchExpressions` list
     * (converted via the `build` method of individual elements) to construct the final
     * label selector specification.
     *
     * @return A new `LabelSelectorSpec` instance containing the configured `matchLabels` and `matchExpressions`.
     */
    internal fun build(): LabelSelectorSpec {
        require(matchLabels != null && matchLabels!!.isNotEmpty()) { "Match labels must be set" }

        return LabelSelectorSpec(matchLabels, matchExpressions?.map { it.build() })
    }

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
    class LabelSelectorRequirementSpecBuilder internal constructor(private var key: String, private var operator: Operator) {
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

    /**
     * A builder class for constructing key-value `matchLabels` entries used in label selection criteria.
     *
     * This class is designed to provide a DSL-style interface for adding single key-value pairs to
     * the `matchLabels` map in the parent `LabelSelectorSpecBuilder`. It enables fluent
     * configuration of label match conditions.
     *
     * The `label` method delegates to the `addMatchLabel` method of the containing `LabelSelectorSpecBuilder`
     * class, which manages the underlying storage and ensures the map is properly initialized.
     */
    inner class MatchLabelListBuilder internal constructor() {
        /**
         * Adds a key-value pair as a label for a match condition in a `matchLabels` map.
         * This method delegates to the `addMatchLabel` function, integrating the label
         * into the underlying selection criteria.
         *
         * @param key The key representing the label name.
         * @param value The value associated with the provided key.
         */
        fun label(key: String, value: String) = addMatchLabel(key, value)
    }

    /**
     * A builder class for creating and configuring a list of match expressions
     * that define selection criteria for resources based on labels.
     *
     * This class provides a DSL-style method to add individual match expressions
     * by specifying their key, operator, and an optional configuration block.
     * Match expressions are used for more complex label-based queries, such as
     * specifying required or prohibited label values.
     *
     * Access to this builder is internal and is typically managed by the
     * containing `LabelSelectorSpecBuilder`.
     *
     * @constructor Creates an instance of `MatchExpressionListBuilder`. This constructor is internal
     * to restrict direct instantiation and ensure proper encapsulation within the
     * label selector specification setup.
     */
    inner class MatchExpressionListBuilder internal constructor() {
        /**
         * Adds a match expression to the list of expressions using the specified key, operator,
         * and an optional configuration block for additional setup.
         *
         * This method facilitates the creation of label-based selection criteria. The `prepare` block
         * provides the ability to define specific requirements related to the match expression.
         *
         * @param key The label key to be used in the match expression.
         * @param operator The operator defining the relationship between the key and its value(s).
         * @param prepare An optional configuration block for customizing the expression's values or setup.
         */
        fun expression(key: String, operator: Operator, prepare: LabelSelectorRequirementSpecBuilder.() -> Unit = {}) =
            addMatchExpression(key, operator, prepare)
    }
}