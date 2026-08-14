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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * A backend an OpenShift Route directs traffic to.
 *
 * @property kind   The kind of the referenced object. OpenShift only accepts [Kind.Service] here.
 * @property name   The name of the referenced Service in the Route's namespace.
 * @property weight The relative share of traffic this backend receives, between 0 and 256. Only
 *                  meaningful when a Route splits traffic across several backends.
 */
@NoArgs
data class RouteTargetSpec(
    val kind: Kind,
    val name: String,
    val weight: Int?
) {
    /**
     * Validates the referenced name and the traffic weight.
     */
    init {
        require(name.isNotBlank()) { "Route target name must not be blank" }
        weight?.let { require(it in 0..256) { "Route target weight must be between 0 and 256, but was $it" } }
    }

    /**
     * The kinds of object an OpenShift Route can target.
     */
    @Suppress("unused")
    enum class Kind {
        /**
         * A Kubernetes Service in the same namespace as the Route.
         */
        Service
    }
}
