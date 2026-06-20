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
import org.pcsoft.framework.kube.kts.api.intern.jackson.RoutePortSpecDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.RoutePortSpecSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Describes the target port of an OpenShift Route.
 *
 * In the rendered YAML this is emitted as a single `targetPort` value which can be either a numeric
 * port ([targetPortNumber]) or a named port ([targetPortName]) of the backing service.
 *
 * @property targetPortName   The name of the target port on the backing service.
 * @property targetPortNumber The numeric target port on the backing service.
 */
@NoArgs
@JsonSerialize(using = RoutePortSpecSerializer::class)
@JsonDeserialize(using = RoutePortSpecDeserializer::class)
data class RoutePortSpec(
    val targetPortName: String?,
    val targetPortNumber: Int?
)
