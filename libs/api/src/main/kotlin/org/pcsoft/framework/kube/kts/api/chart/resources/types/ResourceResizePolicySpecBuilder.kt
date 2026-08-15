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
 * Builder class for creating instances of [ResourceResizePolicySpec].
 *
 * The policy states whether an in-place resize of the given resource can be applied to the running
 * container or whether the container has to be restarted for it to take effect.
 *
 * @constructor Creates an instance of [ResourceResizePolicySpecBuilder] for internal usage.
 * @param resourceName The resource this policy applies to.
 */
class ResourceResizePolicySpecBuilder internal constructor(
    private val resourceName: ResourceResizePolicySpec.ResourceName
) {
    /**
     * What has to happen for a change of the resource to take effect. Defaults to
     * [ResourceResizePolicySpec.RestartPolicy.NotRequired].
     */
    var restartPolicy: ResourceResizePolicySpec.RestartPolicy =
        ResourceResizePolicySpec.RestartPolicy.NotRequired

    /**
     * Constructs and returns a [ResourceResizePolicySpec] instance based on the configured values.
     *
     * @return A [ResourceResizePolicySpec] for the configured resource and restart policy.
     */
    internal fun build(): ResourceResizePolicySpec =
        ResourceResizePolicySpec(resourceName, restartPolicy)
}
