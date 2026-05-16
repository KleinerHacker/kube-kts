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
import java.util.UUID

/**
 * Represents an owner reference to another Kubernetes object.
 *
 * Owner references are used to establish relationships between resources, indicating that one resource "owns" another.
 * This is commonly used for garbage collection and dependency management in Kubernetes.
 *
 * @property apiVersion The API version of the referenced object (e.g., "v1").
 * @property kind The kind of the referenced object (e.g., "Pod", "Deployment").
 * @property name The name of the referenced object.
 * @property uid The unique identifier (UID) of the referenced object.
 * @property controller Indicates whether this reference points to a controller that manages the current object.
 *                      If true, the owner has an exclusive control over the current object and will block its deletion
 *                      if [blockOwnerDeletion] is also set to true.
 * @property blockOwnerDeletion If true, the garbage collector will delete the current object when the owner is deleted.
 */
@NoArgs
data class OwnerReferenceSpec(
    val apiVersion: String,
    val kind: String,
    val name: String,
    val uid: UUID,
    val controller: Boolean?,
    val blockOwnerDeletion: Boolean?,
)