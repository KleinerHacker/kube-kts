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
 * Builder class for creating instances of [ObjectFieldSelectorSpec].
 *
 * The selector points at a field of the pod itself, for example `metadata.name`.
 *
 * @constructor Creates an instance of [ObjectFieldSelectorSpecBuilder] for internal usage.
 * @param fieldPath The selected field, for example `metadata.name`.
 */
class ObjectFieldSelectorSpecBuilder internal constructor(private val fieldPath: String) {
    /**
     * The API version the field path is interpreted against. Defaults to the pod's API version when unset.
     */
    var apiVersion: String? = null

    /**
     * Constructs and returns an [ObjectFieldSelectorSpec] instance based on the configured values.
     *
     * @return An [ObjectFieldSelectorSpec] selecting the configured field.
     */
    internal fun build(): ObjectFieldSelectorSpec = ObjectFieldSelectorSpec(fieldPath, apiVersion)
}
