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
import org.pcsoft.framework.kube.kts.api.intern.jackson.MapToNameValueDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.MapToNameValueSerializer
import org.pcsoft.framework.kube.kts.api.types.PortValue
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Hooks the kubelet executes at defined points in a container's lifetime.
 *
 * Both hooks are executed at least once but may be executed more than once, so their actions should be
 * idempotent. A failing hook kills the container.
 *
 * @property postStart Executed immediately after the container is created. It runs concurrently with the
 *                     container's entrypoint, so it is not guaranteed to complete before it.
 * @property preStop   Executed immediately before the container is terminated. The Pod's termination
 *                     grace period is counted from the start of this hook.
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
     * The common contract of every way a lifecycle hook can act.
     */
    sealed interface Action

    /**
     * Runs a command inside the container.
     *
     * @property command The command and its arguments. Not run through a shell.
     */
    @NoArgs
    data class ExecAction(val command: List<String>) : Action {
        /**
         * Validates that a command is given.
         */
        init {
            require(command.isNotEmpty()) { "Lifecycle command must not be empty" }
        }
    }

    /**
     * Performs an HTTP GET request against the container.
     *
     * @property path        The request path. Defaults to `/` when unset.
     * @property port        The port to connect to, either a number or the name of a container port.
     * @property host        The host to connect to. Defaults to the Pod's IP address.
     * @property scheme      Whether to connect over HTTP or HTTPS. Defaults to HTTP.
     * @property httpHeaders Additional request headers, rendered as a list of `name`/`value` pairs.
     */
    @NoArgs
    data class HttpGetAction(
        val path: String?,
        val port: PortValue<*>,
        val host: String?,
        val scheme: ProtocolScheme?,
        @field:JsonSerialize(using = MapToNameValueSerializer::class)
        @field:JsonDeserialize(using = MapToNameValueDeserializer::class)
        val httpHeaders: Map<String, String>?
    ) : Action

    /**
     * Pauses the container for a fixed duration.
     *
     * Used as a `preStop` hook, this gives load balancers time to stop routing traffic to the Pod before
     * its process is asked to shut down.
     *
     * @property seconds How long to pause.
     */
    @NoArgs
    data class SleepAction(
        @field:JsonSerialize(using = DurationInSecondsSerializer::class)
        @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
        val seconds: Duration
    ) : Action {
        /**
         * Validates that the duration is not negative.
         */
        init {
            require(!seconds.isNegative) { "Sleep duration must not be negative" }
        }
    }
}
