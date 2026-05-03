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

package org.pcsoft.framework.kube.kts.api.chart.template

import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.template.types.MetadataSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents a Kubernetes resource template.
 *
 * @property apiVersion The API version of the resource.
 * @property kind The kind of the resource.
 * @property metadata The metadata for the resource.
 * @property spec The specification for the resource.
 */
@NoArgs
data class TemplateSpec<S>(
    val apiVersion: String,
    val kind: String,
    val metadata: MetadataSpec,
    val spec: S
) : KubeSpec where S : ResourceSpec