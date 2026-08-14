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

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Declares how a container reacts when one of its resources is resized in place.
 *
 * In-place resizing lets Kubernetes change a container's CPU or memory allocation without recreating
 * the Pod. For each resource a container may state whether the change can be applied to the running
 * process or whether the container has to be restarted for it to take effect.
 *
 * @property resourceName  The resource this policy applies to.
 * @property restartPolicy What has to happen for a change of [resourceName] to take effect.
 */
@NoArgs
data class ResourceResizePolicySpec(
    val resourceName: ResourceName,
    val restartPolicy: RestartPolicy
) {
    /**
     * The resources that support in-place resizing.
     */
    @Suppress("unused")
    enum class ResourceName {
        /**
         * The container's CPU allocation.
         */
        @JsonProperty("cpu")
        Cpu,

        /**
         * The container's memory allocation.
         */
        @JsonProperty("memory")
        Memory
    }

    /**
     * Determines whether a container must be restarted to pick up a resized resource.
     */
    @Suppress("unused")
    enum class RestartPolicy {
        /**
         * The resource change is applied to the running container without a restart.
         */
        NotRequired,

        /**
         * The container is restarted so that the resource change takes effect.
         */
        RestartContainer
    }
}
