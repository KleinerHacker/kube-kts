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

import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.ResourceSpec
import org.pcsoft.framework.kube.kts.api.chart.types.MetadataTemplateSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

sealed interface TemplateSpec<S> : KubeSpec where S : ResourceSpec

/**
 * Represents a template specification for defining Kubernetes resources.
 *
 * This class encapsulates the core structure required for Kubernetes resource configuration,
 * including API version, resource kind, metadata, and specification details. It acts as
 * a container for combining metadata and resource-specific configuration into a single
 * declarative representation.
 *
 * @param S The type of the resource specification contained within the template. Must extend [ResourceSpec].
 * @property apiVersion The API version of the Kubernetes resource (e.g., "v1", "apps/v1").
 * @property kind The kind of Kubernetes resource (e.g., ConfigMap, Service, Deployment).
 * @property metadata The metadata definition for the resource, including name, labels, and annotations.
 * @property spec The resource-specific configuration that defines the behavior and structure of the resource.
 */
@NoArgs
data class ExplicitTemplateSpec<S>(
    val apiVersion: String,
    val kind: String,
    val metadata: MetadataTemplateSpec,
    val spec: S
) : TemplateSpec<S> where S : ResourceSpec

/**
 * Represents a flat template specification for defining Kubernetes resources (with flatten spec part).
 *
 * This class encapsulates the core structure required for Kubernetes resource configuration,
 * including API version, resource kind, metadata, and specification details. It acts as
 * a container for combining metadata and resource-specific configuration into a single
 * declarative representation.
 *
 * @param S The type of the resource specification contained within the template. Must extend [ResourceSpec].
 * @property apiVersion The API version of the Kubernetes resource (e.g., "v1", "apps/v1").
 * @property kind The kind of Kubernetes resource (e.g., ConfigMap, Service, Deployment).
 * @property metadata The metadata definition for the resource, including name, labels, and annotations.
 * @property spec The resource-specific configuration that defines the behavior and structure of the resource.
 */
@NoArgs
data class FlatTemplateSpec<S>(
    val apiVersion: String,
    val kind: String,
    val metadata: MetadataTemplateSpec,
    @field:JsonUnwrapped
    val spec: S
) : TemplateSpec<S> where S : ResourceSpec