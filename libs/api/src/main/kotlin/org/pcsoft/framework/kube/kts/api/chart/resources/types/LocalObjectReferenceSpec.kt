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
 * References another object inside the same namespace by name.
 *
 * Kubernetes uses this shape wherever a resource points at a Secret or ConfigMap that is required to
 * live alongside it - for example image pull secrets on a Pod or the credential secret of a volume
 * driver. It renders as a nested object carrying a single `name` key.
 *
 * @property name The name of the referenced object in the same namespace.
 */
@NoArgs
data class LocalObjectReferenceSpec(
    val name: String
) {
    /**
     * Validates that the referenced name is not blank.
     */
    init {
        require(name.isNotBlank()) { "Referenced object name must not be blank" }
    }
}
