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

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.chart.resources.types.TolerationSpec.Effect
import org.pcsoft.framework.kube.kts.api.chart.resources.types.TolerationSpec.Operator
import java.time.Duration

/**
 * A builder class for creating instances of [TolerationSpec]. This class provides a step-by-step
 * mechanism to construct a toleration specification typically used in Kubernetes-like environments
 * for scheduling and execution rules based on taints.
 *
 * @constructor This class is intended for internal use and should not be instantiated directly.
 *
 * @see TolerationSpec
 */
class TolerationSpecBuilder internal constructor() {
    /**
     * The taint key that this toleration applies to. This determines which taint the toleration will match.
     * If null, the toleration applies to all taints with the specified effect.
     */
    var key: String? = null

    /**
     * The taint operator that this toleration applies to. This determines how the taint is matched against the
     * node's taints.
     */
    var operator: Operator? = null

    /**
     * The taint value that this toleration applies to. This determines how the taint is matched against the
     * node's taints.
     */
    var value: String? = null

    /**
     * The effect of the toleration. This determines how the toleration is applied.
     */
    var effect: Effect? = null

    /**
     * The duration of the toleration. This determines how long the toleration is in effect.
     */
    var tolerationSeconds: Duration? = null

    /**
     * Constructs and returns a new instance of [TolerationSpec] based on the properties
     * set in the [TolerationSpecBuilder]. This method aggregates the various properties
     * configured on the builder to create an immutable [TolerationSpec] object.
     *
     * @return A [TolerationSpec] instance representing the toleration configuration.
     */
    internal fun build() = TolerationSpec(key, operator, value, effect, tolerationSeconds)
}