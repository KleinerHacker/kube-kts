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

/**
 * A builder class for constructing [PersistentVolumeClaimRetentionPolicySpec] objects, which control
 * the lifecycle of the PersistentVolumeClaims created from a StatefulSet's `volumeClaimTemplates`.
 *
 * All values are optional.
 *
 * @constructor Creates an instance of `PersistentVolumeClaimRetentionPolicySpecBuilder`.
 */
class PersistentVolumeClaimRetentionPolicySpecBuilder internal constructor() {
    /**
     * The retention behaviour applied when the StatefulSet is deleted.
     */
    var whenDeleted: PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType? = null

    /**
     * The retention behaviour applied when the StatefulSet is scaled down.
     */
    var whenScaled: PersistentVolumeClaimRetentionPolicySpec.RetentionPolicyType? = null

    internal fun build(): PersistentVolumeClaimRetentionPolicySpec =
        PersistentVolumeClaimRetentionPolicySpec(whenDeleted, whenScaled)
}
