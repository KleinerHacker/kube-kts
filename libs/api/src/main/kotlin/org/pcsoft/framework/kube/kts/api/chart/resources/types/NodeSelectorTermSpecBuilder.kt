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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.NodeSelectorTermSpec.NodeSelectorRequirementSpec.Operator

/**
 * A builder class for constructing node selector terms. This class allows the specification
 * of match expressions and match fields to define criteria for selecting nodes.
 *
 * A node selector term consists of two components:
 * - Match expressions: These are conditions based on label keys and their associated values.
 * - Match fields: These are conditions based on resource fields and their associated values.
 *
 * This builder provides methods to add and configure both match expressions and match fields.
 *
 * The following components are included:
 * - `addMatchExpression`: Adds a match expression to the selector based on a key, operator, and a configuration block.
 * - `matchExpressions`: Allows configuration of a list of match expressions.
 * - `addMatchField`: Adds a match field to the selector based on a key, operator, and a configuration block.
 * - `matchFields`: Allows configuration of a list of match fields.
 *
 * Nested builders are provided for constructing complex structures:
 * - `NodeSelectorRequirementSpecBuilder`: Handles the detailed configuration of individual match expressions and match fields.
 * - `ValueListBuilder`: Manages the values associated with requirements.
 * - `MatchExpressionListBuilder`: Provides a DSL for adding multiple match expressions.
 * - `MatchFieldListBuilder`: Provides a DSL for adding multiple match fields.
 */
class NodeSelectorTermSpecBuilder internal constructor() {
    private var matchExpressions: MutableList<NodeSelectorRequirementSpecBuilder>? = null
    private var matchFields: MutableList<NodeSelectorRequirementSpecBuilder>? = null

    /**
     * Adds a match expression to the node selector term's requirements.
     *
     * A match expression specifies a key, an operator, and an optional set of values 
     * that determine matching criteria for selecting nodes.
     *
     * @param key The label key to be used in the match expression.
     * @param operator The operator that defines the matching criteria. Supported operators include:
     * - `In`: The key must have at least one of the provided values.
     * - `NotIn`: The key must not have any of the provided values.
     * - `Exists`: The key must be present, regardless of its value.
     * - `DoesNotExist`: The key must not be present.
     * - `GreaterThan`: The key must have a value greater than the provided value.
     * - `LessThan`: The key must have a value less than the provided value.
     * @param setup A lambda to configure additional properties of the match expression, such as values.
     * 
     * Example:
     * ```kotlin
     * addMatchExpression("kubernetes.io/hostname", Operator.In) {
     *     addValues("node1", "node2")
     * }
     * ```
     */
    fun addMatchExpression(key: String, operator: Operator, setup: NodeSelectorRequirementSpecBuilder.() -> Unit = {}) {
        if (matchExpressions == null) {
            matchExpressions = mutableListOf()
        }
        matchExpressions!!.add(NodeSelectorRequirementSpecBuilder(key, operator).apply(setup))
    }

    /**
     * Configures a list of match expressions for a node selector term. A match expression specifies
     * the criteria for selecting nodes based on their labels.
     *
     * @param setup A lambda used to configure the match expressions. The `MatchExpressionListBuilder` 
     * provides the `match` method to specify individual match expressions.
     * 
     * Example:
     * ```kotlin
     * matchExpressions {
     *     match("kubernetes.io/hostname", Operator.In) {
     *         addValues("node1", "node2")
     *     }
     *     match("node-role", Operator.Exists) { }
     * }
     * ```
     */
    fun matchExpressions(setup: MatchExpressionListBuilder.() -> Unit) =
        MatchExpressionListBuilder().apply(setup)

    /**
     * Adds a match field to the node selector term's requirements.
     * 
     * A match field specifies a key and an operator to define matching criteria for selecting nodes.
     * Additional properties can be configured using the provided lambda function.
     * 
     * @param key The field key to be used in the match field requirement.
     * @param operator The operator that defines the matching criteria.
     * @param setup A lambda to configure additional properties of the match field, such as values.
     * 
     * Example:
     * ```kotlin
     * addMatchField("metadata.name", Operator.In) {
     *     addValues("node1", "node2")
     * }
     * ```
     */
    fun addMatchField(key: String, operator: Operator, setup: NodeSelectorRequirementSpecBuilder.() -> Unit = {}) {
        if (matchFields == null) {
            matchFields = mutableListOf()
        }
        matchFields!!.add(NodeSelectorRequirementSpecBuilder(key, operator).apply(setup))
    }

    /**
     * Configures a list of match fields for a node selector term. A match field specifies 
     * criteria for selecting nodes based on their fields.
     *
     * @param setup A lambda used to configure the match fields. The MatchFieldListBuilder 
     * provides the `match` method to specify individual match fields.
     * 
     * Example:
     * ```kotlin
     * matchFields {
     *     match("metadata.name", Operator.In) {
     *         addValues("node1", "node2")
     *     }
     * }
     * ```
     */
    fun matchFields(setup: MatchFieldListBuilder.() -> Unit) =
        MatchFieldListBuilder().apply(setup)

    /**
     * Builds and returns an instance of `NodeSelectorTermSpec` using the configured
     * match expressions and match fields.
     *
     * This method transforms the current state of the builder, including any configured
     * match expressions and fields, into a finalized `NodeSelectorTermSpec` object. The 
     * resulting specification is typically used to define selection criteria for nodes
     * based on labels or fields.
     *
     * @return A fully constructed `NodeSelectorTermSpec` instance containing the
     * match expressions and match fields, if configured.
     */
    internal fun build() = NodeSelectorTermSpec(matchExpressions?.map { it.build() }, matchFields?.map { it.build() })

    /**
     * Builder for creating and configuring node selector requirements.
     *
     * A node selector requirement specifies criteria for selecting nodes in a Kubernetes cluster.
     * It consists of a label key, an operator that defines the matching logic, and an optional 
     * list of values. The builder pattern is used to configure the requirement's properties.
     *
     * @constructor Creates a new builder with the specified key and operator.
     * @param key The label key used for the requirement.
     * @param operator The operator that defines the matching criteria. 
     */
    class NodeSelectorRequirementSpecBuilder internal constructor(
        private val key: String,
        private val operator: Operator
    ) {
        private var values: MutableList<String>? = null

        /**
         * Adds a value to the list of node selector requirement values.
         *
         * If the values list is not initialized, it will be created before adding the new value.
         *
         * @param value The value to add to the list.
         */
        fun addValue(value: String) {
            if (values == null) {
                values = mutableListOf()
            }
            values!!.add(value)
        }

        /**
         * Adds one or more values to the list of node selector requirement values.
         *
         * If the values list is not initialized, it will be created before adding the new values.
         *
         * @param values The values to add to the list.
         */
        fun addValues(vararg values: String) {
            if (this.values == null) {
                this.values = mutableListOf()
            }
            this.values!!.addAll(values.toList())
        }

        /**
         * Configures the values using the provided setup function.
         *
         * This method allows for customization of the `ValueListBuilder` by invoking the specified lambda on it,
         * enabling the addition of single or multiple values to the list of node selector requirement values.
         *
         * @param setup A lambda with receiver that provides a `ValueListBuilder` instance for configuring values.
         * 
         * Example:
         * ```kotlin
         * values {
         *     value("node1")
         *     values("node2", "node3")
         * }
         * ```
         */
        fun values(setup: ValueListBuilder.() -> Unit) =
            ValueListBuilder().apply(setup)

        /**
         * Builds and returns an instance of `NodeSelectorTermSpec.NodeSelectorRequirementSpec`
         * using the current state of the builder.
         *
         * This method combines the `key`, `operator`, and `values` fields to create
         * a fully configured `NodeSelectorRequirementSpec` object. It is typically used
         * after all relevant properties have been set on the builder to produce
         * a finalized specification of a node selector requirement.
         *
         * @return A configured `NodeSelectorTermSpec.NodeSelectorRequirementSpec` instance.
         */
        internal fun build() = NodeSelectorTermSpec.NodeSelectorRequirementSpec(key, operator, values)

        /**
         * A builder class for constructing and managing a list of values for node selector requirements.
         *
         * This class provides methods to add single or multiple values to the list. It operates 
         * by internally invoking methods that ensure the list is properly initialized before 
         * adding the provided values. 
         *
         * Instances of this class are intended to be used internally for configuring values 
         * within the context of a node selector requirement specification.
         */
        inner class ValueListBuilder internal constructor() {
            /**
             * Adds a single value to the node selector requirements.
             *
             * Internally invokes the method responsible for adding the value to the list.
             *
             * @param value The value to be added to the requirements list.
             */
            fun value(value: String) = addValue(value)

            /**
             * Adds one or more string values for node selector requirements.
             * 
             * This method delegates to an internal function that manages the initialization 
             * and addition of the provided values to the requirement list.
             *
             * @param values The values to be added to the requirements list.
             */
            fun values(vararg values: String) = addValues(*values)
        }
    }

    /**
     * A builder class for configuring a list of match expressions in a node selector term.
     *
     * A match expression is a condition that defines criteria for selecting nodes based on their labels.
     * Each match expression consists of a label key, an operator, and optional values that determine how the key
     * should be evaluated.
     *
     * This builder provides the `match` method to add individual match expressions to the configuration.
     *
     * @constructor Internal constructor to initialize the builder. 
     * Instances are intended to be created and used only within the scope of the containing class.
     */
    inner class MatchExpressionListBuilder internal constructor() {
        /**
         * Adds a match expression to the configuration using the specified key, operator, and setup function.
         *
         * The match expression defines criteria for selecting nodes based on the label key and operator. 
         * Additional properties can be configured using the provided setup lambda.
         *
         * Example:
         * ```kotlin
         * match("kubernetes.io/hostname", Operator.In) {
         *     addValues("node1", "node2")
         * }
         * ```
         *
         * @param key The label key used in the match expression.
         * @param operator The operator that defines the matching criteria.
         * @param setup A lambda function to configure additional properties of the match expression.
         */
        fun match(key: String, operator: Operator, setup: NodeSelectorRequirementSpecBuilder.() -> Unit = {}) =
            addMatchExpression(key, operator, setup)
    }

    /**
     * Builder for configuring a list of match fields in a node selector term.
     *
     * A match field specifies criteria for selecting nodes based on their fields.
     * Each match field consists of a key, an operator, and optional additional properties.
     *
     * @constructor Creates an internal instance of this builder, typically used within a parent builder context.
     */
    inner class MatchFieldListBuilder internal constructor() {
        /**
         * Defines a match field in a node selector term.
         *
         * A match field specifies a key and an operator, with optional additional properties
         * configured using the provided lambda. Match fields are used to define criteria for
         * selecting nodes based on their fields in a Kubernetes cluster.
         *
         * @param key The field key to be used in the match field requirement.
         * @param operator The operator that defines the matching criteria.
         * @param setup A lambda to configure additional properties of the match field.
         * 
         * Example:
         * ```kotlin
         * match("metadata.name", Operator.In) {
         *     addValues("node1", "node2")
         * }
         * ```
         */
        fun match(key: String, operator: Operator, setup: NodeSelectorRequirementSpecBuilder.() -> Unit = {}) =
            addMatchField(key, operator, setup)
    }
}
