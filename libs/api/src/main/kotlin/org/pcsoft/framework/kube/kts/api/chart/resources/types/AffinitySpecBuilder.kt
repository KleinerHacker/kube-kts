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

/**
 * A builder class for constructing an `AffinitySpec` object.
 *
 * This class provides configuration options for specifying node affinity, 
 * pod affinity, and pod anti-affinity rules for Kubernetes workloads.
 * It allows fine-grained control over how pods are scheduled onto nodes or
 * interact with other pods in the cluster.
 *
 * The `AffinitySpecBuilder` should be used in conjunction with its 
 * associated builders (`NodeAffinitySpecBuilder` and `PodAffinitySpecBuilder`) 
 * to configure each type of affinity or anti-affinity.
 *
 * The resulting `AffinitySpec` can be used to define the `affinity` field
 * in a Kubernetes workload specification.
 */
class AffinitySpecBuilder internal constructor() {
    private var nodeAffinity: NodeAffinitySpecBuilder? = null
    private var podAffinity: PodAffinitySpecBuilder? = null
    private var podAntiAffinity: PodAffinitySpecBuilder? = null

    /**
     * Configures the node affinity rules for a Kubernetes workload.
     *
     * This method allows you to define constraints or preferences that influence
     * how pods are scheduled onto nodes. It uses a DSL-style builder, enabling
     * declarative and type-safe configuration within the provided block.
     *
     * Example:
     * ```kotlin
     * nodeAffinity {
     *     requiredDuringSchedulingIgnoredDuringExecution {
     *         term {
     *             matchExpressions {
     *                 expression {
     *                     key = "kubernetes.io/hostname"
     *                     operator = "In"
     *                     values = listOf("node1", "node2")
     *                 }
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda with receiver of type `NodeAffinitySpecBuilder` 
     * that is used to configure the node affinity rules.
     */
    fun nodeAffinity(prepare: NodeAffinitySpecBuilder.() -> Unit) {
        nodeAffinity = NodeAffinitySpecBuilder().apply(prepare)
    }

    /**
     * Configures the pod affinity rules for a Kubernetes workload.
     *
     * This method allows you to define constraints or preferences that influence
     * how pods are scheduled in relation to other pods. It uses a DSL-style builder, 
     * enabling declarative and type-safe configuration within the provided block.
     *
     * Example:
     * ```kotlin
     * podAffinity {
     *     requiredDuringSchedulingIgnoredDuringExecution {
     *         term(topologyKey = "kubernetes.io/hostname") {
     *             labelSelector {
     *                 matchLabels {
     *                     label("app", "nginx")
     *                 }
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda with receiver of type `PodAffinitySpecBuilder` 
     * that is used to configure the pod affinity rules.
     */
    fun podAffinity(prepare: PodAffinitySpecBuilder.() -> Unit) {
        podAffinity = PodAffinitySpecBuilder().apply(prepare)
    }

    /**
     * Configures the pod anti-affinity rules for a Kubernetes workload.
     *
     * This method allows you to define constraints or preferences that influence
     * how pods are scheduled in relation to other pods. It uses a DSL-style builder,
     * enabling declarative and type-safe configuration within the provided block.
     *
     * Example:
     * ```kotlin
     * podAntiAffinity {
     *     requiredDuringSchedulingIgnoredDuringExecution {
     *         term(topologyKey = "kubernetes.io/hostname") {
     *             labelSelector {
     *                 matchLabels {
     *                     label("app", "database")
     *                 }
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda with receiver of type `PodAffinitySpecBuilder`
     * that is used to configure the pod anti-affinity rules.
     */
    fun podAntiAffinity(prepare: PodAffinitySpecBuilder.() -> Unit) {
        podAntiAffinity = PodAffinitySpecBuilder().apply(prepare)
    }

    /**
     * Constructs an `AffinitySpec` instance based on the current state of the builder.
     *
     * This method aggregates the configurations for node affinity, pod affinity, 
     * and pod anti-affinity. It invokes their respective `build` methods, ensuring
     * that the defined rules for affinity and anti-affinity are transformed into
     * a finalized `AffinitySpec` object.
     *
     * @return A new `AffinitySpec` instance encapsulating node, pod, and pod anti-affinity rules.
     */
    fun build() = AffinitySpec(
        nodeAffinity = nodeAffinity?.build(),
        podAffinity = podAffinity?.build(),
        podAntiAffinity = podAntiAffinity?.build()
    )
}
