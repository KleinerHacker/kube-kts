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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeSpec.SourceSpec

/**
 * The common contract of all volume source builders.
 *
 * @param T The concrete [SourceSpec] produced by the implementing builder.
 */
sealed interface SourceSpecBuilder<T : SourceSpec> {
    /**
     * Builds the configured volume source.
     *
     * @return The configured source specification.
     */
    fun build(): T
}
