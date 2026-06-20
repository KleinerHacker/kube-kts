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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.LabelSelectorSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PersistentVolumeClaimRetentionPolicySpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodTemplateSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.resources.types.StatefulSetUpdateStrategySpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeClaimTemplateListSpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.ExplicitTemplateSpecBuilder
import java.time.Duration

/**
 * A builder class for constructing [StatefulSetSpec] objects, which define the specification for a
 * Kubernetes StatefulSet.
 */
class StatefulSetSpecBuilder internal constructor() : ResourceSpecBuilder<StatefulSetSpec> {
    private var selector: LabelSelectorSpecBuilder? = null
    private var template: PodTemplateSpecBuilder? = null
    private var updateStrategy: StatefulSetUpdateStrategySpecBuilder? = null
    private var volumeClaimTemplates: VolumeClaimTemplateListSpecBuilder? = null
    private var persistentVolumeClaimRetentionPolicy: PersistentVolumeClaimRetentionPolicySpecBuilder? = null
    private var ordinals: StatefulSetSpec.OrdinalsSpec? = null

    /**
     * The desired number of replica instances for the StatefulSet. If null, the system default is used.
     */
    var replicas: Int? = null

    /**
     * The name of the governing (usually headless) Service that controls the network domain of the
     * StatefulSet's Pods.
     */
    var serviceName: String? = null

    /**
     * Controls how Pods are created and deleted during scaling operations.
     */
    var podManagementPolicy: StatefulSetSpec.PodManagementPolicy? = null

    /**
     * The maximum number of revisions to retain in the StatefulSet's history for rollback purposes.
     */
    var revisionHistoryLimit: Int? = null

    /**
     * The minimum number of seconds for which a newly created Pod should be ready without any of its
     * containers crashing, for it to be considered available.
     */
    var minReadySeconds: Duration? = null

    /**
     * Configures the label selector identifying the Pods managed by this StatefulSet.
     *
     * @param prepare A lambda with a receiver of [LabelSelectorSpecBuilder].
     */
    fun selector(prepare: LabelSelectorSpecBuilder.() -> Unit) {
        selector = LabelSelectorSpecBuilder().apply(prepare)
    }

    /**
     * Configures the Pod template for the StatefulSet.
     *
     * @param prepare A lambda block used to configure the Pod template via [PodTemplateSpecBuilder].
     */
    fun template(prepare: PodTemplateSpecBuilder.() -> Unit) {
        template = PodTemplateSpecBuilder().apply(prepare)
    }

    /**
     * Configures the update strategy that determines how updates to the [template] are rolled out.
     *
     * @param prepare A lambda with a receiver of [StatefulSetUpdateStrategySpecBuilder].
     */
    fun updateStrategy(prepare: StatefulSetUpdateStrategySpecBuilder.() -> Unit) {
        updateStrategy = StatefulSetUpdateStrategySpecBuilder().apply(prepare)
    }

    /**
     * Configures the PersistentVolumeClaim templates of the StatefulSet. One claim per Pod is
     * provisioned from each template, providing stable per-Pod storage.
     *
     * @param prepare A lambda with a receiver of [VolumeClaimTemplateListSpecBuilder].
     */
    fun volumeClaimTemplates(prepare: VolumeClaimTemplateListSpecBuilder.() -> Unit) {
        volumeClaimTemplates = VolumeClaimTemplateListSpecBuilder().apply(prepare)
    }

    /**
     * Configures the retention policy of the PersistentVolumeClaims created from the
     * [volumeClaimTemplates].
     *
     * @param prepare A lambda with a receiver of [PersistentVolumeClaimRetentionPolicySpecBuilder].
     */
    fun persistentVolumeClaimRetentionPolicy(prepare: PersistentVolumeClaimRetentionPolicySpecBuilder.() -> Unit) {
        persistentVolumeClaimRetentionPolicy = PersistentVolumeClaimRetentionPolicySpecBuilder().apply(prepare)
    }

    /**
     * Configures the ordinal numbering of the StatefulSet's replica indices.
     *
     * @param start The number representing the first replica index.
     */
    fun ordinals(start: Int) {
        ordinals = StatefulSetSpec.OrdinalsSpec(start)
    }

    override fun build(): StatefulSetSpec {
        require(selector != null) { "Selector must be set!" }
        require(template != null) { "Template must be set!" }
        require(!serviceName.isNullOrBlank()) { "Service name must be set!" }

        return StatefulSetSpec(
            replicas = replicas,
            selector = selector!!.build(),
            serviceName = serviceName!!,
            podManagementPolicy = podManagementPolicy,
            updateStrategy = updateStrategy?.build(),
            volumeClaimTemplates = volumeClaimTemplates?.build(),
            persistentVolumeClaimRetentionPolicy = persistentVolumeClaimRetentionPolicy?.build(),
            revisionHistoryLimit = revisionHistoryLimit,
            minReadySeconds = minReadySeconds,
            ordinals = ordinals,
            template = template!!.build()
        )
    }
}

/**
 * Constructs a StatefulSet specification for a Kubernetes StatefulSet resource using a builder-style DSL.
 *
 * @param prepare A lambda with a receiver of type [ExplicitTemplateSpecBuilder] used to configure the
 * metadata and specification for the StatefulSet resource.
 * @return A [ExplicitTemplateSpec] instance containing the configured [StatefulSetSpec].
 *
 * Example:
 * ```kotlin
 * statefulSet {
 *     metadata("my-database") {
 *         namespace = "default"
 *     }
 *     spec {
 *         serviceName = "my-database"
 *         replicas = 3
 *         selector {
 *             matchLabels {
 *                 label("app", "my-database")
 *             }
 *         }
 *         template {
 *             metadata {
 *                 labels {
 *                     label("app", "my-database")
 *                 }
 *             }
 *             spec {
 *                 containers {
 *                     container("db", "postgres") { }
 *                 }
 *             }
 *         }
 *         volumeClaimTemplates {
 *             claim("data") {
 *                 accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
 *                 requests {
 *                     storage = 1.giBytes
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 */
fun statefulSet(prepare: ExplicitTemplateSpecBuilder<StatefulSetSpec, StatefulSetSpecBuilder>.() -> Unit): ExplicitTemplateSpec<StatefulSetSpec> =
    ExplicitTemplateSpecBuilder(StatefulSetSpec.API_VERSION, StatefulSetSpec.KIND, StatefulSetSpecBuilder())
        .apply(prepare)
        .build()
