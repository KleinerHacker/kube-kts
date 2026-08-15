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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.EmptyDirSourceSpec.MediumType
import org.pcsoft.framework.kube.kts.api.types.MemoryValue

/**
 * Builder for an [EmptyDirSourceSpec].
 */
class EmptyDirSourceSpecBuilder internal constructor() : SourceSpecBuilder<EmptyDirSourceSpec> {
    /**
     * Where the storage is backed. Defaults to the node's disk when unset.
     */
    var medium: MediumType? = null

    /**
     * The maximum amount of storage this volume may consume.
     */
    var sizeLimit: MemoryValue? = null

    /**
     * Builds the configured emptyDir source.
     *
     * @return An [EmptyDirSourceSpec] carrying the configured values.
     */
    override fun build() = EmptyDirSourceSpec(medium, sizeLimit)
}
