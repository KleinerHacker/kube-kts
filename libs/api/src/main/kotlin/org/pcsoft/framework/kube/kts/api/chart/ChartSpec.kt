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

package org.pcsoft.framework.kube.kts.api.chart

import com.fasterxml.jackson.annotation.JsonValue
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpec
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.chart.types.MaintainerSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import java.net.URI

/**
 * Represents the specification for a Helm chart including metadata, dependencies, and configuration details.
 *
 * @constructor Creates an instance of [ChartSpec] with the specified properties.
 *
 * @property apiVersion The API version of the chart specification. This defaults to "v2".
 * @property name The name of the chart.
 * @property version The version of the chart.
 * @property kubeVersion The Kubernetes version constraints required by this chart.
 * @property description A brief description of the chart.
 * @property type The type of the chart, specifying whether it is an application or a library.
 * @property keywords A set of keywords associated with the chart for categorization purposes.
 * @property home The URL to the homepage or documentation of the chart.
 * @property sources A list of source URLs related to the chart, such as code repositories.
 * @property dependencies A list of dependencies required by this chart.
 * @property maintainers A list of maintainers responsible for this chart.
 * @property icon The URI of the icon associated with the chart.
 * @property appVersion The version of the application for which this chart is built.
 * @property deprecated A flag indicating whether the chart is deprecated.
 * @property annotations A map of additional metadata annotations associated with the chart.
 */
@NoArgs
data class ChartSpec(
    val apiVersion: String,
    val name: String,
    val version: String,
    val kubeVersion: KubeVersion?,
    val description: String?,
    val type: Type?,
    val keywords: Set<String>?,
    val home: String?,
    val sources: List<URI>?,
    val dependencies: List<DependencySpec>?,
    val maintainers: List<MaintainerSpec>?,
    val icon: URI?,
    val appVersion: String?,
    val deprecated: Boolean?,
    val annotations: Map<String, String>?
) : KubeSpec {
    companion object {
        const val API_VERSION = "v2"
    }

    @Suppress("unused")
    enum class Type {
        Application, Library;

        @JsonValue
        fun toJson(): String {
            return name.lowercase()
        }
    }
}
