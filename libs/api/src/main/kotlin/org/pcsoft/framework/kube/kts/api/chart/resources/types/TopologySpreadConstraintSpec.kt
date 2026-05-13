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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents the configuration specification for TopologySpreadConstraints in Kubernetes.
 *
 * TopologySpreadConstraints allow you to control how pods are distributed across different
 * topology domains (e.g., zones, nodes) to achieve high availability, fault tolerance,
 * and balanced workloads based on cluster-specific needs.
 *
 * @property maxSkew The maximum allowed skew between the number of pods in one topology
 *                   domain and another. A lower value enforces stricter balancing.
 * @property topologyKey The key indicating the topology domain to spread pods across
 *                       (e.g., `failure-domain.beta.kubernetes.io/zone`).
 * @property whenUnsatisfiable Defines the action to be taken if the spread constraint
 *                              cannot be satisfied. Options include `DoNotSchedule` or
 *                              `ScheduleAnyway`.
 * @property labelSelector Criteria for selecting pods that the constraint applies to.
 *                         If null, the constraint applies to all pods.
 * @property minDomains The minimum number of topology domains required to satisfy the
 *                      constraint. This ensures spread constraints are flexible enough
 *                      when fewer domains are available.
 * @property nodeAffinityPolicy Configures whether the constraint should honor or ignore
 *                              node affinity.
 * @property nodeTaintsPolicy Configures whether the constraint should honor or ignore
 *                            node taints.
 * @property matchLabelKeys An optional list of specific label keys used to define additional
 *                          constraints when selecting topology domains.
 */
@NoArgs
data class TopologySpreadConstraintSpec(
    val maxSkew: Int,
    val topologyKey: String,
    val whenUnsatisfiable: WhenUnsatisfiable,
    val labelSelector: LabelSelectorSpec?,
    val minDomains: Int?,
    val nodeAffinityPolicy: NodePolicy?,
    val nodeTaintsPolicy: NodePolicy?,
    val matchLabelKeys: List<String>?
) {
    /**
     * Defines the behavior to apply when a constraint cannot be satisfied.
     */
    @Suppress("unused")
    enum class WhenUnsatisfiable {
        /**
         * Represents a behavior that prevents scheduling when a constraint cannot be satisfied.
         */
        DoNotSchedule,

        /**
         * Represents a behavior that allows scheduling to proceed even if a constraint cannot be satisfied.
         */
        ScheduleAnyway
    }

    /**
     * Represents the policy to apply for nodes when evaluating constraints within the topology spread specification.
     *
     * This enum is used to determine how the scheduler will account for node-level attributes
     * such as labels, taints, or affinities when making scheduling decisions.
     */
    @Suppress("unused")
    enum class NodePolicy {
        /**
         * Specifies the node policy to honor during the evaluation of constraints within the topology spread specification.
         *
         * This policy ensures that node-level attributes, such as labels, taints, or affinities,
         * are respected when the scheduler makes scheduling decisions.
         */
        Honor,

        /**
         * Represents the policy to ignore node-level attributes when evaluating constraints
         * within the topology spread specification.
         *
         * This policy is used by the scheduler to disregard node-level attributes, such as labels,
         * taints, or affinities, during scheduling decisions.
         */
        Ignore
    }
}
