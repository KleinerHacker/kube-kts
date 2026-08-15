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
 * Builder class for creating instances of [TypedObjectReferenceSpec].
 *
 * The reference points at the object a PersistentVolumeClaim is populated from.
 *
 * @constructor Creates an instance of [TypedObjectReferenceSpecBuilder] for internal usage.
 * @param kind The kind of the referenced object.
 * @param name The name of the referenced object.
 */
class TypedObjectReferenceSpecBuilder internal constructor(
    private val kind: String,
    private val name: String
) {
    /**
     * The API group of the referenced object. Core API objects leave this unset.
     */
    var apiGroup: String? = null

    /**
     * The namespace of the referenced object. Only supported for cross-namespace data sources.
     */
    var namespace: String? = null

    /**
     * Constructs and returns a [TypedObjectReferenceSpec] instance based on the configured values.
     *
     * @return A [TypedObjectReferenceSpec] referencing the configured object.
     */
    internal fun build(): TypedObjectReferenceSpec =
        TypedObjectReferenceSpec(kind, name, apiGroup, namespace)
}
