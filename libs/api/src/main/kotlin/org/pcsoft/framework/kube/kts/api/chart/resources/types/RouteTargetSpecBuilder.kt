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
 * Builder for [RouteTargetSpec], used for both the primary backend (`to`) and the entries of
 * `alternateBackends` of an OpenShift Route.
 *
 * @constructor Creates a builder for the Service with the given [name]. Internal by design.
 * @param name The name of the referenced Service.
 */
class RouteTargetSpecBuilder internal constructor(private val name: String) {
    /**
     * The kind of the referenced object. For Routes this is always `Service` (the default).
     */
    var kind: String = "Service"

    /**
     * An optional relative weight (0-256) for weighted traffic splitting.
     */
    var weight: Int? = null

    internal fun build(): RouteTargetSpec {
        require(name.isNotBlank()) { "Service name is required for a route backend" }

        return RouteTargetSpec(kind, name, weight)
    }
}
