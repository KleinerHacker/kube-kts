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
 * Identifies the port of a Service an Ingress backend forwards traffic to.
 *
 * Kubernetes accepts either the port's [number] or its [name], and exactly one of the two has to be
 * given. Referencing by name keeps the Ingress independent of the concrete port numbers the Service
 * exposes.
 *
 * @property name   The name of the Service port. Mutually exclusive with [number].
 * @property number The number of the Service port. Mutually exclusive with [name].
 */
@NoArgs
data class IngressPortSpec(
    val name: String?,
    val number: Int?
) {
    /**
     * Validates that exactly one of name and number is given.
     */
    init {
        require((name == null) != (number == null)) {
            "Exactly one of 'name' and 'number' must be set for an ingress backend port"
        }
        name?.let { require(it.isNotBlank()) { "Port name must not be blank" } }
        number?.let { require(it in 1..65535) { "Port number must be between 1 and 65535, but was $it" } }
    }
}
