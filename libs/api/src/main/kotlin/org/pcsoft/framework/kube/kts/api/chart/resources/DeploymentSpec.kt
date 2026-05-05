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

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.resources.types.DeploymentStrategySpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.LabelSelectorSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodTemplateSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import kotlin.time.Duration

/**
 * Represents the specification for a Kubernetes Deployment resource.
 *
 * A DeploymentSpec defines the desired state for a Deployment, which is a higher-level
 * abstraction for managing Pods. Deployments provide declarative updates for Pods and
 * ReplicaSets while offering features such as automated rollouts, rollbacks, scaling,
 * and more.
 *
 * @property replicas The desired number of replicas (Pod instances) to maintain for this Deployment.
 * If null, the default number of replicas will be determined by Kubernetes.
 *
 * @property selector Defines the label selector used to identify the set of Pods being managed by this Deployment.
 * This is used to associate the Deployment with its corresponding ReplicaSet(s) and Pod(s).
 *
 * @property template Specifies the object that describes the Pods to be created by this Deployment.
 * The `template` includes both metadata for the Pods and their desired state (spec).
 *
 * @property strategy The deployment strategy that determines how updates to Pods are managed.
 * This can be either `Recreate` or `RollingUpdate`, with optional additional configuration
 * for rolling updates.
 *
 * @property minReadySeconds The minimum number of seconds that a Pod should be ready without
 * interruptions before being considered available. This helps ensure stability during updates.
 *
 * @property revisionHistoryLimit The maximum number of ReplicaSets to retain in the history
 * for rollback purposes. Older ReplicaSets exceeding this limit will be automatically deleted.
 *
 * @property paused Indicates whether the Deployment is currently paused. A paused Deployment
 * will halt rollouts and updates without affecting the existing Pods or ReplicaSets.
 *
 * @property progressDeadlineSeconds The maximum duration that updates to Pods are allowed to
 * take within this Deployment. If the rollout or update does not complete within this duration,
 * the Deployment will record an error.
 */
@NoArgs
data class DeploymentSpec(
    val replicas: Int?,
    val selector: LabelSelectorSpec,
    val template: PodTemplateSpec,
    val strategy: DeploymentStrategySpec?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val minReadySeconds: Duration?,
    val revisionHistoryLimit: Int?,
    val paused: Boolean?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val progressDeadlineSeconds: Duration?
) : ResourceSpec {
    companion object {
        /**
         * Represents the kind of Kubernetes resource associated with this specification.
         * This constant identifies the resource as a Deployment.
         */
        const val KIND = "Deployment"

        /**
         * Defines the API version for the Kubernetes Deployment resource.
         *
         * This constant specifies the API group and version under which the
         * Deployment resource is defined in Kubernetes. It is used to indicate
         * the applicable version of the Kubernetes API for the resource.
         *
         * Value: "apps/v1"
         */
        const val API_VERSION = "apps/v1"
    }
}