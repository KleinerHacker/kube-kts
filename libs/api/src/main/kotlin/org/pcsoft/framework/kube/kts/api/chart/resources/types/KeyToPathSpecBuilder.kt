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
 * Builder for a single [KeyToPathSpec].
 *
 * @constructor Creates a builder for the given key and target path.
 * @param key  The key to project.
 * @param path The relative path the key's value is written to.
 */
class KeyToPathSpecBuilder internal constructor(private val key: String, private val path: String) {
    /**
     * The POSIX permissions of the created file. Falls back to the volume's default mode when unset.
     */
    var mode: Int? = null

    /**
     * Builds the configured key mapping.
     *
     * @return A [KeyToPathSpec] carrying the configured values.
     */
    internal fun build() = KeyToPathSpec(key, path, mode)
}
