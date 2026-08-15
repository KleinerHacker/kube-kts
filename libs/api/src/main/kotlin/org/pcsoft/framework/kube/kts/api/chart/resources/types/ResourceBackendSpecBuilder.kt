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
 * Builder for creating a resource-based backend specification.
 *
 * This builder is used to configure a backend that points to a generic Kubernetes resource.
 * It requires the resource's name and kind as mandatory parameters and allows optional API
 * group configuration. The resulting `ResourceBackendSpec` object can be used to represent
 * Kubernetes resources as backends in various configurations.
 *
 * All values are optional.
 *
 * @constructor Creates a new instance of the builder with the specified resource name and kind.
 * @param name The name of the resource.
 * @param kind The kind of the resource to be referenced in the backend.
 *
 * @see BackendSpecBuilder
 * @see ResourceBackendSpec
 */
class ResourceBackendSpecBuilder internal constructor(private val name: String, private val kind: String) :
    BackendSpecBuilder() {
    /**
     * The API group of the resource.
     */
    var apiGroup: String? = null

    override fun build(): BackendSpec {
        require(name.isNotBlank()) { "Resource name is required for resource backend" }
        require(kind.isNotBlank()) { "Resource kind is required for resource backend" }

        return ResourceBackendSpec(name, kind, apiGroup)
    }
}
