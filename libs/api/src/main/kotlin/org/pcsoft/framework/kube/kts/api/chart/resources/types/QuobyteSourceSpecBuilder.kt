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
 * Builder for a [QuobyteSourceSpec].
 *
 * @constructor Creates a builder for the given Quobyte volume.
 * @param registry The Quobyte registry.
 * @param volume   The name of the Quobyte volume.
 */
@Suppress("DEPRECATION")
class QuobyteSourceSpecBuilder internal constructor(
    private val registry: String,
    private val volume: String
) : SourceSpecBuilder<QuobyteSourceSpec> {
    /**
     * If true, the volume is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * The user to map the mount to.
     */
    var user: String? = null

    /**
     * The group to map the mount to.
     */
    var group: String? = null

    /**
     * The tenant owning the volume in a multi-tenant installation.
     */
    var tenant: String? = null

    /**
     * Builds the configured Quobyte source.
     *
     * @return A [QuobyteSourceSpec] carrying the configured values.
     */
    override fun build(): QuobyteSourceSpec = QuobyteSourceSpec(registry, volume, readOnly, user, group, tenant)
}
