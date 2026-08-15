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
 * Builder class for creating instances of [ResourceClaimReferenceSpec].
 *
 * The reference points at an entry of the pod's `resourceClaims` list and optionally narrows the
 * usage down to a single request inside that claim.
 *
 * @constructor Creates an instance of [ResourceClaimReferenceSpecBuilder] for internal usage.
 * @param name The name of the entry in the pod's `resourceClaims` list this reference points to.
 */
class ResourceClaimReferenceSpecBuilder internal constructor(private val name: String) {
    /**
     * An optional name of a specific request inside the referenced claim. If unset, the container may
     * use every request of the claim.
     */
    var request: String? = null

    /**
     * Constructs and returns a [ResourceClaimReferenceSpec] instance based on the configured values.
     *
     * @return A [ResourceClaimReferenceSpec] referencing the configured claim.
     */
    internal fun build(): ResourceClaimReferenceSpec =
        ResourceClaimReferenceSpec(name, request)
}
