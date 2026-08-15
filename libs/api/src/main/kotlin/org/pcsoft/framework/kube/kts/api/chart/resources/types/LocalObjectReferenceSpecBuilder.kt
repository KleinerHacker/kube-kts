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
 * Builder class for creating instances of [LocalObjectReferenceSpec].
 *
 * A local object reference points at another object - usually a Secret or a ConfigMap - inside the
 * same namespace. The builder therefore only carries the name of the referenced object.
 *
 * @constructor Creates an instance of [LocalObjectReferenceSpecBuilder] for internal usage.
 * @param name The name of the referenced object in the same namespace.
 */
class LocalObjectReferenceSpecBuilder internal constructor(private val name: String) {
    /**
     * Constructs and returns a [LocalObjectReferenceSpec] instance for the configured name.
     *
     * @return A [LocalObjectReferenceSpec] referencing the configured object.
     */
    internal fun build(): LocalObjectReferenceSpec {
        require(name.isNotBlank()) { "Referenced object name must not be blank" }

        return LocalObjectReferenceSpec(name)
    }
}
