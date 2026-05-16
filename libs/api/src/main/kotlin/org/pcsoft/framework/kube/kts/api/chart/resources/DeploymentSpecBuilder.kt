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

import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpecBuilder

class DeploymentSpecBuilder internal constructor() : ResourceSpecBuilder<DeploymentSpec> {
    var replicas: Int? = null
    var minReadySeconds: Int? = null
    var revisionHistoryLimit: Int? = null
    var paused: Boolean? = null
    var progressDeadlineSeconds: Int? = null

    override fun build(): DeploymentSpec {
        TODO("Not yet implemented")
    }
}

fun deployment(prepare: TemplateSpecBuilder<DeploymentSpec, DeploymentSpecBuilder>.() -> Unit): TemplateSpec<DeploymentSpec> =
    TemplateSpecBuilder(DeploymentSpec.API_VERSION, DeploymentSpec.KIND, DeploymentSpecBuilder())
        .apply(prepare)
        .build()