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
import org.pcsoft.framework.kube.kts.api.intern.jackson.*
import org.pcsoft.framework.kube.kts.api.types.PortValue
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Describes a health check the kubelet performs against a container.
 *
 * A probe pairs one [action] - how the check is carried out - with the timing that governs how often it
 * runs and how many results are needed before the container's state flips.
 *
 * In the rendered YAML the action is not nested under an `action` key; it becomes a sibling of the
 * timing fields whose key names the action type. This flattening is performed by [ProbeSpecSerializer].
 *
 * @property action                        How the check is performed.
 * @property initialDelaySeconds           How long to wait after the container starts before probing.
 * @property periodSeconds                 How often the probe runs.
 * @property timeoutSeconds                How long a single probe attempt may take before it counts as failed.
 * @property successThreshold              How many consecutive successes are needed to count as healthy.
 *                                         Must be 1 for liveness and startup probes.
 * @property failureThreshold              How many consecutive failures are needed to count as unhealthy.
 * @property terminationGracePeriodSeconds Overrides the Pod's grace period when this probe forces a restart.
 */
@NoArgs
@JsonSerialize(using = ProbeSpecSerializer::class)
@JsonDeserialize(using = ProbeSpecDeserializer::class)
data class ProbeSpec(
    val action: ProbeAction,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val initialDelaySeconds: Duration?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val periodSeconds: Duration?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val timeoutSeconds: Duration?,
    val successThreshold: Int?,
    val failureThreshold: Int?,
    @field:JsonSerialize(using = DurationInSecondsSerializer::class)
    @field:JsonDeserialize(using = DurationInSecondsDeserializer::class)
    val terminationGracePeriodSeconds: Duration?
) {
    /**
     * The common contract of every way a probe can check a container.
     */
    sealed interface ProbeAction

    /**
     * Runs a command inside the container and treats exit code 0 as success.
     *
     * @property command The command and its arguments. Not run through a shell.
     */
    @NoArgs
    data class ExecAction(
        val command: List<String>
    ) : ProbeAction {
        /**
         * Validates that a command is given.
         */
        init {
            require(command.isNotEmpty()) { "Probe command must not be empty" }
        }
    }

    /**
     * Performs an HTTP GET request and treats a status code from 200 to 399 as success.
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
    ) : ProbeAction

    /**
     * Opens a TCP connection and treats a successful handshake as success.
     *
     * @property port The port to connect to, either a number or the name of a container port.
     * @property host The host to connect to.
     */
    @NoArgs
    data class TCPSocketAction(
        val port: PortValue<*>,
        @Deprecated(
            message = "This field has an unfortunate history in Kubernetes. It was never fully implemented and " +
                    "its behavior was inconsistent across different Kubernetes versions. Use of this field is " +
                    "discouraged and it may be removed in future versions."
        )
        val host: String?
    ) : ProbeAction

    /**
     * Calls the standard gRPC health checking service and treats a `SERVING` response as success.
     *
     * @property port    The port the gRPC server listens on. Named ports are not supported here.
     * @property service The name of the service to query. Defaults to the server's overall health.
     */
    @NoArgs
    data class GRPCAction(
        val port: Int,
        val service: String?
    ) : ProbeAction {
        /**
         * Validates that the port is within the valid range.
         */
        init {
            require(port in 1..65535) { "Port must be between 1 and 65535, but was $port" }
        }
    }
}
