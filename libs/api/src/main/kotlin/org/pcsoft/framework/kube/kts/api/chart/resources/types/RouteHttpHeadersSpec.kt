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

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * HTTP header manipulations an OpenShift Route applies as traffic passes through the router.
 *
 * @property actions The header actions applied to requests and responses.
 */
@NoArgs
data class RouteHttpHeadersSpec(
    val actions: RouteHttpHeaderActionsSpec
)

/**
 * The header actions applied to requests and responses of a Route.
 *
 * @property request  Actions applied to requests before they reach the backend.
 * @property response Actions applied to responses before they reach the client.
 */
@NoArgs
data class RouteHttpHeaderActionsSpec(
    val request: List<RouteHttpHeaderSpec>?,
    val response: List<RouteHttpHeaderSpec>?
)

/**
 * A single header manipulation performed by the router.
 *
 * @property name   The name of the header to manipulate. Some headers, such as `Host` and
 *                  `Strict-Transport-Security`, are managed by the router and cannot be changed here.
 * @property action What the router does with the header.
 */
@NoArgs
data class RouteHttpHeaderSpec(
    val name: String,
    val action: Action
) {
    /**
     * Validates that the header name is not blank.
     */
    init {
        require(name.isNotBlank()) { "Header name must not be blank" }
    }

    /**
     * What the router does with a header.
     *
     * A [type] of [Type.Set] requires [set] to be given; [Type.Delete] requires it to be absent.
     *
     * @property type What kind of manipulation is performed.
     * @property set  The value to set. Only used together with [Type.Set].
     */
    @NoArgs
    data class Action(
        val type: Type,
        val set: SetAction?
    ) {
        /**
         * Validates that the value matches the chosen action type.
         */
        init {
            require((type == Type.Set) == (set != null)) {
                "'set' must be given exactly when the action type is 'Set'"
            }
        }
    }

    /**
     * The value a header is set to.
     *
     * @property value The header value. May contain HAProxy dynamic value expressions.
     */
    @NoArgs
    data class SetAction(
        val value: String
    )

    /**
     * The kinds of header manipulation a Route supports.
     */
    @Suppress("unused")
    enum class Type {
        /**
         * The header is set to a fixed value, replacing any existing one.
         */
        @JsonProperty("Set")
        Set,

        /**
         * The header is removed.
         */
        @JsonProperty("Delete")
        Delete
    }
}
