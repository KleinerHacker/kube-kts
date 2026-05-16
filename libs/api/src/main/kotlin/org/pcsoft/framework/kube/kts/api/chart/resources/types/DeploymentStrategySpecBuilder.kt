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

import org.pcsoft.framework.kube.kts.api.types.RelativeValue

/**
 * A builder class for constructing instances of the `DeploymentStrategySpec`.
 *
 * This builder allows the configuration of deployment strategies, including type selection
 * and the optional specification of rolling update parameters. The `DeploymentStrategySpec`
 * defines how updates to resources (e.g., Pods) are managed during a deployment process.
 *
 * The builder supports two types of strategies:
 * - `Recreate`: All existing resources are terminated before new ones are created.
 * - `RollingUpdate`: Resources are updated incrementally, allowing a controlled and gradual
 *   replacement process.
 *
 * All values are optional.
 *
 * @constructor Creates an instance of `DeploymentStrategySpecBuilder`.
 */
class DeploymentStrategySpecBuilder internal constructor(){
    private var rollingUpdate: RollingUpdateDeploymentSpecBuilder? = null

    /**
     * Specifies the type of deployment strategy used for managing updates to resources in a deployment.
     *
     * This property is optional and can be set to `null` if no specific strategy is to be defined.
     */
    var type: DeploymentStrategySpec.Type? = null

    /**
     * Configures the rolling update strategy for resource deployment.
     *
     * This method allows the specification of parameters for a rolling update,
     * providing controlled and incremental updates of resources during deployment.
     *
     * @param prepare The configuration block used to define the settings for the rolling update.
     *                This block applies to an instance of `RollingUpdateDeploymentSpecBuilder`,
     *                where properties such as `maxUnavailable` and `maxSurge` can be configured.
     */
    fun rollingUpdate(prepare: RollingUpdateDeploymentSpecBuilder.() -> Unit) {
        rollingUpdate = RollingUpdateDeploymentSpecBuilder().apply(prepare)
    }

    /**
     * Builds and returns an instance of `DeploymentStrategySpec` representing the deployment strategy configuration.
     *
     * This method combines the deployment strategy type and rolling update configuration, if specified,
     * into a single `DeploymentStrategySpec` object. The resulting object is used to define how updates
     * to resources should be managed, supporting both `Recreate` and `RollingUpdate` strategies.
     *
     * @return An instance of `DeploymentStrategySpec` representing the configured deployment strategy.
     */
    internal fun build(): DeploymentStrategySpec = DeploymentStrategySpec(
        type = type,
        rollingUpdate = rollingUpdate?.build()
    )

    /**
     * Builder class for configuring the rolling update deployment strategy in a Kubernetes deployment.
     *
     * This class allows specification of parameters for a rolling update strategy, which ensures incremental
     * updates to the desired state of resources. It supports configuring limits on both the maximum number
     * of unavailable resources and the maximum surge of additional resources during the update process.
     *
     * @constructor Creates an instance of `RollingUpdateDeploymentSpecBuilder` for internal use by its parent builder class.
     *
     * @property maxUnavailable The maximum number of resources that can be unavailable during the rolling update.
     * This can be specified as either an absolute value or a percentage relative to the desired state.
     *
     * @property maxSurge The maximum number of additional resources that can be created temporarily during the
     * rolling update. This can also be expressed as either an absolute value or a percentage relative to the desired state.
     *
     * @return An instance of `DeploymentStrategySpec.RollingUpdateDeploymentSpec` with the configured parameters.
     */
    class RollingUpdateDeploymentSpecBuilder internal constructor() {
        var maxUnavailable: RelativeValue<*,*>? = null
        var maxSurge: RelativeValue<*,*>? = null

        /**
         * Builds and returns an instance of `DeploymentStrategySpec.RollingUpdateDeploymentSpec`
         * configured with the provided `maxUnavailable` and `maxSurge` values.
         *
         * @return A `DeploymentStrategySpec.RollingUpdateDeploymentSpec` instance containing the
         *         rolling update strategy configuration, specifying the maximum number of unavailable
         *         resources and the maximum surge of additional resources during the update process.
         */
        internal fun build(): DeploymentStrategySpec.RollingUpdateDeploymentSpec =
            DeploymentStrategySpec.RollingUpdateDeploymentSpec(maxUnavailable, maxSurge)
    }
}