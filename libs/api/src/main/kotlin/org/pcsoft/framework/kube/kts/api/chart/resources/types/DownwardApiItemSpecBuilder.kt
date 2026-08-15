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
 * Builder class for creating instances of [DownwardApiItemSpec].
 *
 * An item writes either a field of the pod ([fieldRef]) or a resource value of one of its containers
 * ([resourceFieldRef]) into a single file of the volume. Exactly one of both has to be configured.
 *
 * @constructor Creates an instance of [DownwardApiItemSpecBuilder] for internal usage.
 * @param path The relative path of the created file.
 */
class DownwardApiItemSpecBuilder internal constructor(private val path: String) {
    private var fieldRef: ObjectFieldSelectorSpecBuilder? = null
    private var resourceFieldRef: ResourceFieldSelectorSpecBuilder? = null

    /**
     * The POSIX permissions of the created file. Falls back to the volume's default mode when unset.
     */
    var mode: Int? = null

    /**
     * Writes a field of the pod itself into the file.
     *
     * @param fieldPath The selected field, for example `metadata.name`.
     * @param prepare   Configures the [ObjectFieldSelectorSpecBuilder].
     */
    fun fieldRef(fieldPath: String, prepare: ObjectFieldSelectorSpecBuilder.() -> Unit = {}) {
        fieldRef = ObjectFieldSelectorSpecBuilder(fieldPath).apply(prepare)
        resourceFieldRef = null
    }

    /**
     * Writes a resource request or limit of a container into the file.
     *
     * @param resource The selected resource, for example `limits.cpu`.
     * @param prepare  Configures the [ResourceFieldSelectorSpecBuilder].
     */
    fun resourceFieldRef(resource: String, prepare: ResourceFieldSelectorSpecBuilder.() -> Unit = {}) {
        resourceFieldRef = ResourceFieldSelectorSpecBuilder(resource).apply(prepare)
        fieldRef = null
    }

    /**
     * Constructs and returns a [DownwardApiItemSpec] instance based on the configured values.
     *
     * @return A [DownwardApiItemSpec] carrying the configured selector.
     */
    internal fun build(): DownwardApiItemSpec {
        require(fieldRef != null || resourceFieldRef != null) {
            "A downward API item must select either a field or a resource field"
        }

        return DownwardApiItemSpec(path, fieldRef?.build(), resourceFieldRef?.build(), mode)
    }
}
