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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents a label selector specification for a Kubernetes resource.
 *
 * A label selector is used to specify a set of criteria for selecting resources
 * based on their labels. It supports two modes of filtering:
 *
 * 1. **Match Labels**: A map of key-value pairs that directly match labels on resources.
 * 2. **Match Expressions**: A list of label selector requirements that provide a more
 *    flexible query mechanism based on keys, operators, and values.
 *
 * @property matchLabels Defines a key-value map where each pair must match exactly
 *                       with the labels on a resource for it to be selected.
 * @property matchExpressions A list of label selector requirements that allow for
 *                             more complex selection criteria using operators like `In`,
 *                             `NotIn`, `Exists`, and `DoesNotExist`.
 */
@NoArgs
data class LabelSelectorSpec(
    val matchLabels: Map<String, String>?,
    val matchExpressions: List<LabelSelectorRequirementSpec>?
)

/**
 * Represents a requirement for a label selector, defining key-value constraints on labels.
 *
 * This class is used in scenarios where specific label matching criteria are needed,
 * such as in Kubernetes resources like pod affinity/anti-affinity or selecting nodes
 * based on key-value pairs.
 *
 * @property key The label key to match against.
 * @property operator The operator that defines how the key-value comparison is evaluated.
 *                    The available operators are `In`, `NotIn`, `Exists`, and `DoesNotExist`.
 * @property values A list of values to compare against the key, applicable for operators like `In` or `NotIn`.
 *                  This can be null for operators like `Exists` or `DoesNotExist` where no value comparison is required.
 */
@NoArgs
data class LabelSelectorRequirementSpec(
    val key: String,
    val operator: Operator,
    val values: List<String>?
) {
    /**
     * Defines the set of operators used to specify matching criteria
     * in label selector requirements.
     *
     * The `Operator` enum represents the logical relationship between a label key
     * and its associated values or existence state. It is typically used in contexts
     * such as Kubernetes label selectors for pods, nodes, or other resources.
     */
    @Suppress("unused")
    enum class Operator {
        /**
         * Represents the `In` operator used to specify that a given label key must have
         * at least one of the provided values in its label selector requirement.
         *
         * This operator is typically used in contexts that require matching specific
         * label values for resources such as pods, nodes, or other Kubernetes resources.
         */
        In,

        /**
         * Represents the `NotIn` operator used to specify that a given label key must not have
         * any of the provided values in its label selector requirement.
         *
         * This operator is typically used in contexts where a resource must exclude specific
         * label values in environments like Kubernetes, ensuring a clear distinction between
         * desired and undesired configurations.
         */
        NotIn,

        /**
         * Represents the `Exists` operator used to indicate that a given label key
         * must be present in a label selector requirement, regardless of its value.
         *
         * This operator is typically used in contexts that require validation of the presence
         * of a specific label key for resources such as pods, nodes, or other Kubernetes resources.
         */
        Exists,

        /**
         * Represents the `DoesNotExist` operator used to indicate that a given label key
         * must not be present in a label selector requirement.
         *
         * This operator is typically used in contexts where the non-existence of a specific label key
         * is required for resources such as pods, nodes, or other Kubernetes resources.
         */
        DoesNotExist
    }
}