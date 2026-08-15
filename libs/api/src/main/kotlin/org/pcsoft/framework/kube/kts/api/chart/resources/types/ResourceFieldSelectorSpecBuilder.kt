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
 * Builder class for creating instances of [ResourceFieldSelectorSpec].
 *
 * The selector points at a resource request or limit of a container, for example `limits.cpu`.
 *
 * @constructor Creates an instance of [ResourceFieldSelectorSpecBuilder] for internal usage.
 * @param resource The selected resource, for example `limits.cpu`.
 */
class ResourceFieldSelectorSpecBuilder internal constructor(private val resource: String) {
    /**
     * The container the resource is read from. Defaults to the container the value is exposed in.
     */
    var containerName: String? = null

    /**
     * The unit the value is divided by, for example `1Mi`.
     */
    var divisor: String? = null

    /**
     * Constructs and returns a [ResourceFieldSelectorSpec] instance based on the configured values.
     *
     * @return A [ResourceFieldSelectorSpec] selecting the configured resource.
     */
    internal fun build(): ResourceFieldSelectorSpec =
        ResourceFieldSelectorSpec(resource, containerName, divisor)
}
