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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.HostPathSourceSpec.Type

/**
 * Builder for a [HostPathSourceSpec].
 *
 * @constructor Creates a builder for the given host path.
 * @param path The absolute path on the host node.
 */
class HostPathSourceSpecBuilder internal constructor(private val path: String) :
    SourceSpecBuilder<HostPathSourceSpec> {
    /**
     * What the path is expected to be, and whether it may be created on demand.
     */
    var type: Type? = null

    /**
     * Builds the configured hostPath source.
     *
     * @return A [HostPathSourceSpec] carrying the configured values.
     */
    override fun build(): HostPathSourceSpec = HostPathSourceSpec(path, type)
}
