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

package org.pcsoft.framework.kube.kts.api.chart.template

import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec

/**
 * The common contract of all template builders.
 *
 * A template builder wraps a resource specification together with the metadata of the rendered
 * Kubernetes manifest. The concrete implementations differ in how much of that manifest is written
 * explicitly by the user.
 *
 * @param S The type of the resource specification contained within the template.
 * @param B The type of the template produced by the builder.
 */
sealed interface TemplateSpecBuilder<S, B> : KubeSpec where S : ResourceSpec, B : TemplateSpec<S> {
    /**
     * Builds the configured template.
     *
     * @return The configured template specification.
     */
    fun build(): B
}
