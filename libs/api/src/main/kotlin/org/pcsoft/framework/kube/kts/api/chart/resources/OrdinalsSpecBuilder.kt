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

package org.pcsoft.framework.kube.kts.api.chart.resources

/**
 * Builder class for creating instances of [StatefulSetSpec.OrdinalsSpec].
 *
 * The ordinals control the number the first replica of a StatefulSet is named after.
 *
 * @constructor Creates an instance of [OrdinalsSpecBuilder] for internal usage.
 */
class OrdinalsSpecBuilder internal constructor() {
    /**
     * The ordinal of the first replica. Defaults to `0` when unset.
     */
    var start: Int? = null

    /**
     * Constructs and returns a [StatefulSetSpec.OrdinalsSpec] instance based on the configured value.
     *
     * @return A [StatefulSetSpec.OrdinalsSpec] carrying the configured start ordinal.
     */
    internal fun build(): StatefulSetSpec.OrdinalsSpec = StatefulSetSpec.OrdinalsSpec(start)
}
