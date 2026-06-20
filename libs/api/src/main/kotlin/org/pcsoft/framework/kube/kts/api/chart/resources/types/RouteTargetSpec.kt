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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * References a backend of an OpenShift Route, used for both the primary backend (`to`) and the
 * entries of `alternateBackends`.
 *
 * @property kind   The kind of the referenced object. For Routes this is always `Service`.
 * @property name   The name of the referenced Service.
 * @property weight An optional relative weight (0-256) used for weighted traffic splitting between
 *                  the primary backend and alternate backends.
 */
@NoArgs
data class RouteTargetSpec(
    val kind: String,
    val name: String,
    val weight: Int?
)
