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

import java.util.UUID

/**
 * Builder for creating [OwnerReferenceSpec] instances.
 *
 * This builder allows configuration of owner reference properties such as API version,
 * kind, name, and UID. It also supports optional settings like controller flag and
 * block owner deletion behavior.
 *
 * All values are optional.
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
class OwnerReferenceSpecBuilder internal constructor(private val apiVersion: String, private val kind: String, private val name: String, private val uid: UUID) {
    var controller: Boolean? = null
    var blockOwnerDeletion: Boolean? = null

    internal fun build(): OwnerReferenceSpec {
        require(apiVersion.isNotBlank()) { "API Version must not be empty" }
        require(kind.isNotBlank()) { "Kind must not be empty" }
        require(name.isNotBlank()) { "Name must not be empty" }

        return OwnerReferenceSpec(apiVersion, kind, name, uid, controller, blockOwnerDeletion)
    }

}