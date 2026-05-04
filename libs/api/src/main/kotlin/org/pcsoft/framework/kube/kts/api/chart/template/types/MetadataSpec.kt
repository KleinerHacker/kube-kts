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

package org.pcsoft.framework.kube.kts.api.chart.template.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents a specification for Kubernetes object metadata.
 *
 * This data class encapsulates various metadata attributes that can be applied to Kubernetes resources,
 * such as labels, annotations, owner references, and timestamps. It is used to define the metadata
 * structure for Kubernetes objects declaratively.
 *
 * @property name The name of the Kubernetes resource.
 * @property generateName A prefix to use when generating a unique name for the resource.
 * @property namespace The namespace where the resource should be created.
 * @property labels Key-value pairs that can be used to organize and select resources.
 * @property annotations Additional metadata that can be attached to the resource, often used by tools or systems.
 * @property finalizers A list of finalizers that control how the resource is deleted.
 * @property ownerReferences References to other Kubernetes objects that own this object.
 * @property clusterName Deprecated. The name of the cluster where the resource resides (deprecated in favor of
 *                       infrastructure-level handling).
 */
@NoArgs
data class MetadataSpec(
    val name: String,
    val generateName: String?,
    val namespace: String?,
    val labels: Map<String, String>?,
    val annotations: Map<String, String>?,
    val finalizers: List<String>?,
    val ownerReferences: List<OwnerReferenceSpec>?,
    @Deprecated(
        message = "Cluster name is deprecated. Kubernetes resources are scoped to a single cluster. " +
                "Cluster identification should be handled at the infrastructure level.",
        replaceWith = ReplaceWith(""),
        level = DeprecationLevel.WARNING
    ) val clusterName: String?,
)