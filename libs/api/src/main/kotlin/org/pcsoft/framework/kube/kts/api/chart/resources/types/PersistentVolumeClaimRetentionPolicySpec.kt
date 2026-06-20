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

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Describes the lifecycle of the PersistentVolumeClaims created from a StatefulSet's
 * `volumeClaimTemplates`. It controls whether those claims are kept or deleted when the owning
 * StatefulSet is deleted or scaled down.
 *
 * @property whenDeleted The retention behaviour applied when the StatefulSet is deleted.
 * @property whenScaled The retention behaviour applied when the StatefulSet is scaled down.
 */
@NoArgs
data class PersistentVolumeClaimRetentionPolicySpec(
    val whenDeleted: RetentionPolicyType?,
    val whenScaled: RetentionPolicyType?
) {
    /**
     * The retention behaviour for the PersistentVolumeClaims of a StatefulSet.
     */
    @Suppress("unused")
    enum class RetentionPolicyType {
        /**
         * The claims are kept. This is the default behaviour.
         */
        Retain,

        /**
         * The claims are deleted together with the StatefulSet or when scaling down.
         */
        Delete
    }
}
