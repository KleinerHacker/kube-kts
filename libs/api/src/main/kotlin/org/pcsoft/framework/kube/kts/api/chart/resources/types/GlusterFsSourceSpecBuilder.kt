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
 * Builder for a [GlusterFsSourceSpec].
 *
 * @constructor Creates a builder for the given Gluster volume.
 * @param endpoints The name of the Endpoints object describing the Gluster cluster.
 * @param path      The name of the Gluster volume.
 */
class GlusterFsSourceSpecBuilder internal constructor(
    private val endpoints: String,
    private val path: String
) : SourceSpecBuilder<GlusterFsSourceSpec> {
    /**
     * If true, the volume is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * Builds the configured GlusterFS source.
     *
     * @return A [GlusterFsSourceSpec] carrying the configured values.
     */
    override fun build(): GlusterFsSourceSpec = GlusterFsSourceSpec(endpoints, path, readOnly)
}
