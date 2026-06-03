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

package org.pcsoft.framework.kube.kts.api.chart.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents the base specification for metadata.
 *
 * This sealed class serves as a foundation for metadata-related entities and provides
 * common properties used for storing key-value pairs in the form of labels and annotations.
 *
 * @property labels A map of key-value pairs typically used to organize, categorize, or
 *                  identify resources.
 * @property annotations A map of key-value pairs intended for storing arbitrary metadata
 *                       or non-identifying information about resources.
 */
@NoArgs
sealed class MetadataBaseSpec(
    val labels: Map<String, String>?,
    val annotations: Map<String, String>?,

)

/**
 * Defines the specification for metadata templates associated with Kubernetes resources.
 *
 * This class extends [MetadataBaseSpec] and serves as a blueprint for constructing metadata
 * with support for labels, annotations, and additional attributes specific to Kubernetes objects.
 *
 * @property name The unique name of the resource. This field is required and must be unique
 *                within the namespace.
 * @property generateName An optional prefix used to generate a unique name for the resource
 *                        when the `name` field is not specified. Kubernetes appends a random
 *                        suffix to this prefix to create a unique name.
 * @property namespace The namespace in which the resource is created. If omitted, the resource
 *                     will be assigned to the default namespace.
 * @property finalizers A list of finalizers that ensure specific cleanup actions are performed
 *                      before the resource is deleted. Used to manage resource lifecycle and cleanup.
 * @property ownerReferences A collection of [OwnerReferenceSpec] that identify other Kubernetes
 *                           objects that own or control this resource. This is used to set
 *                           deletion dependencies and manage ownership relationships.
 * @property clusterName A deprecated field for specifying the name of the cluster to which
 *                       this object belongs. Resources are now scoped to a single cluster, and
 *                       cluster identity should be managed at the infrastructure level.
 */
@NoArgs
class MetadataTemplateSpec(
    val name: String,
    val generateName: String?,
    val namespace: String?,
    labels: Map<String, String>?,
    annotations: Map<String, String>?,
    val finalizers: List<String>?,
    val ownerReferences: List<OwnerReferenceSpec>?,
    @Deprecated(
        message = "Cluster name is deprecated. Kubernetes resources are scoped to a single cluster. " +
                "Cluster identification should be handled at the infrastructure level.",
        replaceWith = ReplaceWith(""),
        level = DeprecationLevel.WARNING
    ) val clusterName: String?,
): MetadataBaseSpec(labels, annotations)

/**
 * Represents the metadata configuration for a pod template specification.
 *
 * This class extends the base metadata specification, allowing for the definition
 * of labels and annotations associated with a pod template. It provides a mechanism
 * to supply metadata for organizing, identifying, or attaching arbitrary information
 * to pod templates.
 *
 * Labels are commonly used to group and select resources efficiently, while annotations
 * are intended for adding non-identifying metadata.
 *
 * @constructor Creates an instance of MetadataPodTemplateSpec with the specified labels
 * and annotations.
 * @param labels A map of key-value pairs used to identify and categorize the pod template.
 * @param annotations A map of key-value pairs used for arbitrary metadata or additional
 * descriptive information about the pod template.
 */
@NoArgs
class MetadataPodSpec(
    labels: Map<String, String>?,
    annotations: Map<String, String>?,
): MetadataBaseSpec(labels, annotations)