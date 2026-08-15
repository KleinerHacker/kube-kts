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
 * Builder class for creating instances of [RouteHttpHeadersSpec].
 *
 * The builder wraps the header actions an OpenShift Route applies as traffic passes through the
 * router. The actions themselves are configured through [actions] or through the shortcut functions
 * of this builder.
 *
 * @constructor Creates an instance of [RouteHttpHeadersSpecBuilder] for internal usage.
 */
class RouteHttpHeadersSpecBuilder internal constructor() {
    private val actions: RouteHttpHeaderActionsSpecBuilder = RouteHttpHeaderActionsSpecBuilder()

    /**
     * Configures the header actions applied to requests and responses.
     *
     * Example:
     * ```kotlin
     * actions {
     *     addRequestHeader("X-Forwarded-Proto") { set("https") }
     *     addResponseHeader("Server") { delete() }
     * }
     * ```
     *
     * @param prepare A lambda with a receiver of [RouteHttpHeaderActionsSpecBuilder] to define the actions.
     */
    fun actions(prepare: RouteHttpHeaderActionsSpecBuilder.() -> Unit) {
        actions.apply(prepare)
    }

    /**
     * Sets a header on requests forwarded to the backend, replacing any existing value.
     *
     * @param name  The name of the header.
     * @param value The value to set.
     */
    fun setRequestHeader(name: String, value: String) = actions.setRequestHeader(name, value)

    /**
     * Removes a header from requests forwarded to the backend.
     *
     * @param name The name of the header.
     */
    fun deleteRequestHeader(name: String) = actions.deleteRequestHeader(name)

    /**
     * Sets a header on responses returned to the client, replacing any existing value.
     *
     * @param name  The name of the header.
     * @param value The value to set.
     */
    fun setResponseHeader(name: String, value: String) = actions.setResponseHeader(name, value)

    /**
     * Removes a header from responses returned to the client.
     *
     * @param name The name of the header.
     */
    fun deleteResponseHeader(name: String) = actions.deleteResponseHeader(name)

    /**
     * Constructs and returns a [RouteHttpHeadersSpec] instance based on the configured actions.
     *
     * @return A [RouteHttpHeadersSpec] carrying the configured header actions.
     */
    internal fun build(): RouteHttpHeadersSpec = RouteHttpHeadersSpec(actions.build())
}
