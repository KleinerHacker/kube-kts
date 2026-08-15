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
 * Builder class for creating instances of [RoutePortSpec].
 *
 * This builder allows for specifying the target port of an OpenShift Route either by its name or by
 * its number, but not both simultaneously.
 *
 * There are no more fields inside.
 */
class RoutePortSpecBuilder private constructor(
    private val targetPortName: String?,
    private val targetPortNumber: Int?
) {
    /**
     * Secondary constructor for `RoutePortSpecBuilder` that initializes an instance based on the
     * given target port name without a target port number.
     *
     * @param targetPortName The name of the target port on the backing service.
     */
    internal constructor(targetPortName: String) : this(targetPortName, null)

    /**
     * Secondary constructor for `RoutePortSpecBuilder` that initializes an instance based on the
     * given target port number without a target port name.
     *
     * @param targetPortNumber The numeric target port on the backing service.
     */
    internal constructor(targetPortNumber: Int) : this(null, targetPortNumber)

    /**
     * Constructs and returns a [RoutePortSpec] instance based on the configured target port.
     *
     * @return A [RoutePortSpec] referencing the target port either by name or by number.
     */
    internal fun build(): RoutePortSpec {
        require(targetPortName != null || targetPortNumber != null) {
            "Route port must have either a target port name or a target port number"
        }
        if (targetPortName != null) {
            require(targetPortName.isNotBlank()) { "Target port name must not be blank" }
        }
        if (targetPortNumber != null) {
            require(targetPortNumber > 0) { "Target port number must be positive" }
            require(targetPortNumber <= 65535) { "Target port number must be less or equals to 65535" }
        }

        return RoutePortSpec(targetPortName, targetPortNumber)
    }
}
