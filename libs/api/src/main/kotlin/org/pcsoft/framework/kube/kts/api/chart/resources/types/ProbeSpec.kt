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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.DurationInSecondsSerializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.ProbeSpecSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.Duration

/**
 * Represents the configuration of a probe, which is used to check the health or readiness
 * of a container or a service in a Kubernetes environment. Probes can be configured with
 * different actions and thresholds to determine success or failure based on specific criteria.
 *
 * @property action The action to be performed by the probe, such as executing a command,
 *                  performing an HTTP GET request, or checking a TCP socket connection.
 * @property initialDelaySeconds The number of seconds to wait after the container starts
 *                               before performing the first probe.
 * @property periodSeconds The interval, in seconds, between consecutive probes.
 * @property timeoutSeconds The maximum amount of time, in seconds, allowed for a probe to
 *                          complete before it is considered a failure.
 * @property successThreshold The minimum number of consecutive successful probe executions required
 *                            before the service or container is considered healthy.
 * @property failureThreshold The number of consecutive probe failures required before the service
 *                            or container is considered unhealthy.
 * @property terminationGracePeriodSeconds The amount of time, in seconds, to allow for the graceful
 *                                         termination of the container when a termination signal is sent.
 */
@NoArgs
@JsonSerialize(using = ProbeSpecSerializer::class)
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
     * Represents an action or behavior tied to a Kubernetes probe.
     *
     * A `ProbeAction` defines the operations that the Kubernetes system
     * should perform to assess the health or readiness of a specific container.
     *
     * Common implementations include custom commands, HTTP checks, or TCP socket checks
     * that determine the state of the container.
     *
     * Implementing classes or objects should specify the concrete mechanism of the probe action.
     */
    sealed interface ProbeAction

    /**
     * Represents an action that executes a specific command as part of a Kubernetes probe.
     *
     * The `ExecAction` defines an operation to be performed using a list of command-line arguments.
     * This action is typically used in Kubernetes readiness or liveness probes to assess the state of a container.
     *
     * For example, the command may validate a specific condition or output certain values
     * that indicate the health or functionality of the container.
     *
     * @property command A list of strings representing the command to be executed.
     */
    @NoArgs
    data class ExecAction(
        val command: List<String>
    ) : ProbeAction

    /**
     * Represents an HTTP GET action used to perform health checks or readiness probes on containers.
     *
     * This class defines the parameters for HTTP GET requests, including URL components,
     * headers, and the underlying scheme (HTTP or HTTPS). It is primarily used in Kubernetes
     * probe configurations to determine the operational state of a container by querying a specific endpoint.
     *
     * @property path The path portion of the URL to invoke the HTTP GET request. Can be null.
     * @property port The port number to which the HTTP GET request should be sent.
     * @property host The hostname or IP address of the target endpoint. Can be null.
     * @property scheme The transport layer scheme to use for the request (HTTP or HTTPS). Can be null.
     * @property httpHeaders A map of HTTP headers to include in the request, where the key represents
     * the header name and the value represents the corresponding header value. Can be null.
     */
    @NoArgs
    data class HttpGetAction(
        val path: String?,
        val port: Int,
        val host: String?,
        val scheme: Scheme?,
        val httpHeaders: Map<String, String>?
    ) : ProbeAction

    /**
     * Represents a TCP socket-based action for a Kubernetes probe.
     *
     * This action defines a health or readiness check using a TCP connection
     * to the specified port and host. It is part of the probe mechanism that
     * monitors the state of a container in the Kubernetes ecosystem.
     *
     * @property port The port number to which the TCP connection will be established.
     * @property host The hostname or IP address of the target for the TCP connection.
     *                This field is optional and may be null, in which case it is assumed
     *                that the connection targets the localhost of the container.
     * @constructor Marks the class as requiring a no-arguments constructor.
     */
    @NoArgs
    data class TCPSocketAction(
        val port: Int,
        @Deprecated(message = "This field has an unfortunate history in Kubernetes. It was never fully implemented and " +
                "its behavior was inconsistent across different Kubernetes versions. Use of this field is discouraged " +
                "and it may be removed in future versions.")
        val host: String?
    ) : ProbeAction

    /**
     * Represents a gRPC probe action, used to check the health or readiness of a container.
     *
     * A gRPC probe interacts with a gRPC service running inside a container to determine its state.
     * The gRPC connection is established using the specified port and service name.
     *
     * @property port The port number on which the gRPC service is running inside the container.
     * @property service The name of the gRPC service to contact. Can be null if no specific service name is required.
     */
    @NoArgs
    data class GRPCAction(
        val port: Int,
        val service: String?
    ) : ProbeAction

    /**
     * Represents HTTP or HTTPS schemes.
     *
     * Scheme is used to define the protocol scheme for URLs or
     * network requests, typically in the context of ingress rules,
     * service endpoints, or API clients.
     *
     * It is often used for distinguishing between unsecured (HTTP)
     * and secured (HTTPS) communication.
     */
    enum class Scheme {
        /**
         * Represents the HTTP protocol scheme.
         *
         * HTTP is a widely used protocol for transferring hypertext and other resources
         * on the web. It operates over a connectionless and stateless model. Commonly used
         * for unsecured (non-encrypted) communication in network configurations.
         */
        HTTP,

        /**
         * Represents the HTTPS protocol scheme.
         *
         * HTTPS is a secured version of the Hypertext Transfer Protocol (HTTP),
         * often used for encrypted communication over the web. It leverages
         * Transport Layer Security (TLS) or Secure Sockets Layer (SSL) protocols
         * to ensure secure data exchange between clients and servers by providing
         * authentication, data integrity, and encryption.
         *
         * Commonly used in scenarios requiring secure communication, such as
         * API interactions, web applications, and service endpoints.
         */
        HTTPS
    }
}
