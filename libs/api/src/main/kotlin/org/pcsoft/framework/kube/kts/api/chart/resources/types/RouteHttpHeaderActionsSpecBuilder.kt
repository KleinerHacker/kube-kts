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
 * Builder class for creating instances of [RouteHttpHeaderActionsSpec].
 *
 * The actions are split into manipulations applied to requests before they reach the backend and
 * manipulations applied to responses before they reach the client.
 *
 * @constructor Creates an instance of [RouteHttpHeaderActionsSpecBuilder] for internal usage.
 */
class RouteHttpHeaderActionsSpecBuilder internal constructor() {
    private val request: MutableList<RouteHttpHeaderSpecBuilder> = mutableListOf()
    private val response: MutableList<RouteHttpHeaderSpecBuilder> = mutableListOf()

    /**
     * Adds a manipulation applied to requests forwarded to the backend.
     *
     * Example:
     * ```kotlin
     * addRequestHeader("X-Forwarded-Proto") { set("https") }
     * ```
     *
     * @param name    The name of the header to manipulate.
     * @param prepare A lambda with a receiver of [RouteHttpHeaderSpecBuilder] to define the action.
     */
    fun addRequestHeader(name: String, prepare: RouteHttpHeaderSpecBuilder.() -> Unit) {
        request.add(RouteHttpHeaderSpecBuilder(name).apply(prepare))
    }

    /**
     * Adds a manipulation applied to responses returned to the client.
     *
     * Example:
     * ```kotlin
     * addResponseHeader("Server") { delete() }
     * ```
     *
     * @param name    The name of the header to manipulate.
     * @param prepare A lambda with a receiver of [RouteHttpHeaderSpecBuilder] to define the action.
     */
    fun addResponseHeader(name: String, prepare: RouteHttpHeaderSpecBuilder.() -> Unit) {
        response.add(RouteHttpHeaderSpecBuilder(name).apply(prepare))
    }

    /**
     * Sets a header on requests forwarded to the backend, replacing any existing value.
     *
     * @param name  The name of the header.
     * @param value The value to set.
     */
    fun setRequestHeader(name: String, value: String) = addRequestHeader(name) { set(value) }

    /**
     * Removes a header from requests forwarded to the backend.
     *
     * @param name The name of the header.
     */
    fun deleteRequestHeader(name: String) = addRequestHeader(name) { delete() }

    /**
     * Sets a header on responses returned to the client, replacing any existing value.
     *
     * @param name  The name of the header.
     * @param value The value to set.
     */
    fun setResponseHeader(name: String, value: String) = addResponseHeader(name) { set(value) }

    /**
     * Removes a header from responses returned to the client.
     *
     * @param name The name of the header.
     */
    fun deleteResponseHeader(name: String) = addResponseHeader(name) { delete() }

    /**
     * Constructs and returns a [RouteHttpHeaderActionsSpec] instance based on the configured actions.
     *
     * @return A [RouteHttpHeaderActionsSpec] carrying the configured request and response actions.
     */
    internal fun build(): RouteHttpHeaderActionsSpec = RouteHttpHeaderActionsSpec(
        request = request.map { it.build() }.takeIf { it.isNotEmpty() },
        response = response.map { it.build() }.takeIf { it.isNotEmpty() }
    )
}
