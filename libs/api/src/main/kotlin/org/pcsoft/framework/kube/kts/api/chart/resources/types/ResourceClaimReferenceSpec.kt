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
 * References a resource claim declared on the Pod from within a container's resource requirements.
 *
 * Dynamic Resource Allocation lets a Pod declare claims for specialised hardware - for example GPUs -
 * under `spec.resourceClaims`. A container opts into using such a claim by referencing it here.
 *
 * @property name    The name of the entry in the Pod's `resourceClaims` list this reference points to.
 * @property request An optional name of a specific request inside the referenced claim. If unset, the
 *                   container may use every request of the claim.
 */
@NoArgs
data class ResourceClaimReferenceSpec(
    val name: String,
    val request: String?
) {
    /**
     * Validates that the referenced names are not blank.
     */
    init {
        require(name.isNotBlank()) { "Resource claim name must not be blank" }
        request?.let { require(it.isNotBlank()) { "Resource claim request must not be blank" } }
    }
}
