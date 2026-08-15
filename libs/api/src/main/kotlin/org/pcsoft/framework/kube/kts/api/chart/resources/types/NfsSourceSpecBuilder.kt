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
 * Builder for an [NfsSourceSpec].
 *
 * @constructor Creates a builder for the given NFS export.
 * @param server The hostname or IP address of the NFS server.
 * @param path   The absolute path of the export.
 */
class NfsSourceSpecBuilder internal constructor(private val server: String, private val path: String) :
    SourceSpecBuilder<NfsSourceSpec> {
    /**
     * If true, the export is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * Builds the configured NFS source.
     *
     * @return An [NfsSourceSpec] carrying the configured values.
     */
    override fun build(): NfsSourceSpec = NfsSourceSpec(server, path, readOnly)
}
