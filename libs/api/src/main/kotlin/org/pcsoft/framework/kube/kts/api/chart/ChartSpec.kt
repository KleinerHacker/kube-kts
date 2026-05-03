/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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
 * Represents the specification of a Helm chart.
 *
 * @property apiVersion Specifies the API version being used.
 * @property name The name of the chart.
 * @property version The version of the chart.
 * @property kubeVersion The compatible Kubernetes version(s) for the chart.
 * @property description A brief description of the chart.
 * @property type The type of chart, which can be either Application or Library.
 * @property keywords A set of keywords associated with the chart.
 * @property home The URL of the chart's homepage.
 * @property sources A list of URLs associated with the chart's source repository.
 * @property dependencies A list of dependencies required by the chart.
 * @property maintainers A list of maintainers for the chart along with their contact information.
 * @property icon The URI of the chart's icon.
 * @property appVersion The version of the application the chart installs.
 * @property deprecated Indicates if the chart is deprecated.
 * @property annotations A map of annotations or metadata related to the chart.
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
