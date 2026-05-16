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
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.LifecycleSpecActionDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.LifecycleSpecActionSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Defines the lifecycle configuration for handling container events within a Kubernetes pod.
 *
 * This class specifies custom actions that can be executed at specific points in a container's lifecycle,
 * such as immediately after startup (`postStart`) or just before termination (`preStop`).
 *
 * @property postStart The action to execute after the container starts. Can be null if no action is defined.
 * @property preStop The action to execute before the container stops. Can be null if no action is defined.
 */
@NoArgs
data class LifecycleSpec(
    @field:JsonSerialize(using = LifecycleSpecActionSerializer::class)
    @field:JsonDeserialize(using = LifecycleSpecActionDeserializer::class)
    val postStart: Action?,
    @field:JsonSerialize(using = LifecycleSpecActionSerializer::class)
    @field:JsonDeserialize(using = LifecycleSpecActionDeserializer::class)
    val preStop: Action?,
) {
    /**
     * Represents an action that can be executed as part of a container's lifecycle.
     *
     * This sealed interface is implemented by various action types that define
     * specific behaviors or operations to be performed, such as executing commands,
     * sending HTTP requests, or performing timed delays.
     *
     * It is commonly used in lifecycle configurations to define actions for
     * `postStart` and `preStop` events in a container's lifecycle.
     */
    sealed interface Action

    /**
     * Represents an action to execute a command within a container.
     *
     * This action is commonly used as part of a container's lifecycle
     * configuration, such as defining custom behavior during the `postStart`
     * or `preStop` events.
     *
     * @property command The list of strings that compose the command to be executed.
     * Each element represents a portion of the command, where the first element
     * typically indicates the executable, and subsequent elements are its arguments.
     */
    @NoArgs
    data class ExecAction(val command: List<String>) : Action

    /**
     * Represents an HTTP GET action for a container's lifecycle event.
     *
     * This class defines an action that performs an HTTP GET request to a specified endpoint.
     * It is commonly used within lifecycle configurations to trigger HTTP-based interactions
     * during the `postStart` or `preStop` events of a container.
     *
     * @property path The URI path to send the GET request to. Can be null if not specified.
     * @property port The port on the target host to send the GET request to.
     * @property host The hostname for the target, which may be an IP address or domain name. Can be null if default resolution is used.
     * @property scheme The protocol scheme (HTTP or HTTPS) for the request. Can be null if the default scheme is applied.
     * @property httpHeaders A map of HTTP headers to include in the GET request. Can be null if no custom headers are required.
     */
    @NoArgs
    data class HttpGetAction(
        val path: String?,
        val port: Int,
        val host: String?,
        val scheme: ProtocolScheme?,
        val httpHeaders: Map<String, String>?
    ) : Action

    /**
     * Represents an action that pauses execution for a specified duration.
     *
     * This action is used in container lifecycle configurations to introduce delays.
     * The duration is expressed in seconds and serialized/deserialized using custom
     * serializers.
     *
     * @property seconds The duration of the sleep action represented as a `Duration` object.
     *                   This value is serialized to and deserialized from the total number
     *                   of seconds.
     */
    @NoArgs
    data class SleepAction(
        @field:JsonSerialize(using = DurationInSecondsSerializer::class)
        @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
        val seconds: Duration
    ) : Action
}
