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

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.resources.types.DeploymentStrategySpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.resources.types.LabelSelectorSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder
import java.time.Duration

/**
 * A builder class for constructing `DeploymentSpec` objects, which define the specification for a Kubernetes Deployment.
 */
class DeploymentSpecBuilder internal constructor() : ResourceSpecBuilder<DeploymentSpec> {
    private var selector: LabelSelectorSpecBuilder? = null
    private var template: PodTemplateSpecBuilder? = null
    private var strategy: DeploymentStrategySpecBuilder? = null

    /**
     * The number of desired replica instances for the deployment.
     *
     * Specifies the target number of pod replicas that should be running for this
     * deployment. If set to `null`, the default value defined by the system will
     * be used. This parameter allows the deployment to scale to the desired state.
     */
    var replicas: Int? = null

    /**
     * The minimum number of seconds for which a newly created pod should be ready
     * without any of its container crashing, for it to be considered available.
     * Defaults to 0 (pod will be considered available as soon as it is ready).
     */
    var minReadySeconds: Duration? = null

    /**
     * The maximum number of old ReplicaSets to retain to allow rollback.
     * Defaults to 10.
     */
    var revisionHistoryLimit: Int? = null

    /**
     * Indicates whether the deployment is paused and will not be processed by the deployment controller.
     */
    var paused: Boolean? = null

    /**
     * The duration in seconds the deployment controller should wait for a deployment to become available.
     * If exceeded, the deployment will be marked as failed.
     */
    var progressDeadlineSeconds: Duration? = null

    /**
     * Configures the label selector for the deployment.
     *
     * @param prepare a lambda with receiver that allows building a label selector configuration.
     */
    fun selector(prepare: LabelSelectorSpecBuilder.() -> Unit) {
        selector = LabelSelectorSpecBuilder().apply(prepare)
    }

    /**
     * Configures the pod template for the deployment.
     *
     * This method allows specifying the pod template configuration using a lambda block.
     * The provided block operates on an instance of [PodTemplateSpecBuilder], enabling the
     * setup of metadata and specifications for the pod template.
     *
     * @param prepare A lambda block used to configure the pod template via [PodTemplateSpecBuilder].
     * The block provides a fluent API to define metadata and pod specifications.
     */
    fun template(prepare: PodTemplateSpecBuilder.() -> Unit) {
        template = PodTemplateSpecBuilder().apply(prepare)
    }

    /**
     * Configures the deployment strategy for the deployment specification.
     *
     * This method allows specifying the deployment strategy using a lambda block. The provided block operates on
     * an instance of [DeploymentStrategySpecBuilder], enabling the setup of parameters such as the type of the
     * strategy (`Recreate` or `RollingUpdate`) and any optional rolling update configurations.
     *
     * @param prepare A lambda block that is applied to an instance of [DeploymentStrategySpecBuilder] for defining
     * the deployment strategy. The block provides a fluent API to configure the strategy type and, if applicable,
     * the rolling update parameters.
     */
    fun strategy(prepare: DeploymentStrategySpecBuilder.() -> Unit) {
        strategy = DeploymentStrategySpecBuilder().apply(prepare)
    }

    /**
     * Builds and returns a fully configured instance of [DeploymentSpec] based on the current state of the builder.
     *
     * The method validates that the required properties `selector` and `template` are not null before proceeding.
     * If any of these properties are missing, an [IllegalStateException] is thrown.
     *
     * @return A new [DeploymentSpec] instance containing the configured selector, template, strategy, and other
     * deployment-related settings.
     * @throws IllegalStateException If the required `selector` or `template` properties are not set.
     */
    override fun build(): DeploymentSpec {
        require(selector != null) { "Selector must be set!" }
        require(template != null) { "Template must be set!" }

        return DeploymentSpec(
            selector = selector!!.build(),
            template = template!!.build(),
            strategy = strategy?.build(),
            replicas = replicas,
            minReadySeconds = minReadySeconds,
            revisionHistoryLimit = revisionHistoryLimit,
            paused = paused,
            progressDeadlineSeconds = progressDeadlineSeconds
        )
    }
}

/**
 * Constructs a deployment specification for a Kubernetes Deployment resource.
 * This function allows users to define the desired state and configuration
 * details for a Deployment using a builder-style DSL.
 *
 * @param prepare A lambda with a receiver of type [ExplicitTemplateSpecBuilder]. This lambda is used
 * to configure the metadata and specification for the Deployment resource.
 * The receiver provides methods to set properties like metadata, labels, replicas,
 * pod template, and other settings relevant to the Deployment.
 *
 * @return A [ExplicitTemplateSpec] instance containing the configured [DeploymentSpec].
 * This object can be used to define and apply a Deployment resource in a Kubernetes cluster.
 */
fun deployment(prepare: ExplicitTemplateSpecBuilder<DeploymentSpec, DeploymentSpecBuilder>.() -> Unit): ExplicitTemplateSpec<DeploymentSpec> =
    ExplicitTemplateSpecBuilder(DeploymentSpec.API_VERSION, DeploymentSpec.KIND, DeploymentSpecBuilder())
        .apply(prepare)
        .build()