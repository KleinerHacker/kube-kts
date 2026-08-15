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
 * Builder class for creating instances of [RouteHttpHeaderSpec].
 *
 * A header specification describes a single manipulation the OpenShift router performs on a header
 * as traffic passes through it. Either [set] or [delete] has to be called to define the action.
 *
 * @constructor Creates an instance of [RouteHttpHeaderSpecBuilder] for internal usage.
 * @param name The name of the header to manipulate.
 */
class RouteHttpHeaderSpecBuilder internal constructor(private val name: String) {
    private var action: RouteHttpHeaderSpec.Action? = null

    /**
     * Sets the header to a fixed value, replacing any existing one.
     *
     * @param value The header value. May contain HAProxy dynamic value expressions.
     */
    fun set(value: String) {
        action = RouteHttpHeaderSpec.Action(
            RouteHttpHeaderSpec.Type.Set,
            RouteHttpHeaderSpec.SetAction(value)
        )
    }

    /**
     * Removes the header.
     */
    fun delete() {
        action = RouteHttpHeaderSpec.Action(RouteHttpHeaderSpec.Type.Delete, null)
    }

    /**
     * Constructs and returns a [RouteHttpHeaderSpec] instance based on the configured action.
     *
     * @return A [RouteHttpHeaderSpec] carrying the configured header manipulation.
     */
    internal fun build(): RouteHttpHeaderSpec {
        require(action != null) { "A header action must be defined via 'set' or 'delete'" }

        return RouteHttpHeaderSpec(name, action!!)
    }
}
