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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.ProbeSpec.ProbeAction
import org.pcsoft.framework.kube.kts.api.chart.resources.types.ProbeSpec.Scheme
import java.time.Duration

/**
 * A builder class responsible for constructing a `ProbeSpec` instance.
 * It allows configuration of various properties and actions for a Kubernetes-style probe.
 *
 * The class provides methods to define the specific action for the probe,
 * such as executing a command, performing an HTTP GET request, using a TCP socket,
 * or utilizing gRPC communication. It also enables customization of timing and threshold-related
 * parameters for the probe.
 *
 * @constructor Creates an internal instance of `ProbeSpecBuilder`.
 */
class ProbeSpecBuilder internal constructor() {
    private var action: ProbeActionBuilder<*>? = null

    /**
     * Specifies the initial delay in seconds before performing a probe.
     *
     * This property is used to configure the amount of time to wait
     * after the container starts, before executing the first probe.
     * If not set, the default value depends on the system's configuration.
     */
    var initialDelaySeconds: Duration? = null

    /**
     * Specifies the interval, in seconds, between successive probe executions.
     *
     * This variable determines how frequently the system should execute the configured
     * health or readiness probe to monitor the state of a resource. If set to null,
     * the default interval defined by the system will be used.
     */
    var periodSeconds: Duration? = null

    /**
     * Specifies the timeout duration in seconds for a given probe.
     *
     * This property defines the maximum time a probe is allowed to run before it is considered as failed.
     * A null value implies no specific timeout is set, and the default timeout behavior is used.
     */
    var timeoutSeconds: Duration? = null

    /**
     * Specifies the minimum number of consecutive successful checks required to consider the probe successful.
     *
     * This property determines how many succeeding health checks (e.g., HTTP, TCP, gRPC) must pass
     * before the resource is deemed in a "ready" or "healthy" state. It's commonly used as part of
     * Kubernetes readiness and liveness probe configurations.
     *
     * A `null` value indicates that no specific threshold is defined, and the default behavior of the probe
     * implementation will be applied.
     */
    var successThreshold: Int? = null

    /**
     * Specifies the maximum number of consecutive failures allowed during a probe execution
     * before the associated container is considered unhealthy.
     *
     * This value determines how many times a probe can fail before Kubernetes takes action,
     * such as restarting the container or marking it as unready. If set to null, the default
     * behavior will depend on the Kubernetes configuration or client implementation.
     */
    var failureThreshold: Int? = null

    /**
     * Specifies the termination grace period (in seconds) for a resource.
     *
     * This property is used to define the amount of time a system should wait
     * for processes to terminate gracefully before forcefully shutting them down.
     * A `null` value indicates that the default termination grace period should be used.
     */
    var terminationGracePeriodSeconds: Duration? = null

    /**
     * Configures and applies an `ExecAction` for executing a command within the container.
     *
     * This method allows you to define the behavior of an `ExecAction` by providing a configuration
     * block that specifies the command to be executed. Once configured, the constructed action is
     * assigned to the internal state for further use.
     *
     * @param prepare A lambda block used to configure an `ExecActionBuilder`. Within this block, you can set up
     * the command to be executed by using the `command(vararg command: String)` method.
     */
    fun exec(prepare: ExecActionBuilder.() -> Unit) =
        ExecActionBuilder().apply(prepare).let { action = it }

    /**
     * Configures and applies an `HttpGetAction` for performing an HTTP GET request.
     *
     * This method allows you to set up the behavior of the HTTP GET action, specifying parameters such as
     * the port and an optional configuration block for further customization, like setting headers or
     * adjusting the path.
     *
     * @param port The target port for the HTTP GET request. Must be in the range of 1 to 65535.
     * @param prepare A lambda block used to configure an `HttpGetActionBuilder`. Within this block, you can
     *                set properties like `path`, `host`, `scheme`, and add HTTP headers using the `addHttpHeader`
     *                or `httpHeaders` methods.
     */
    fun httpGet(port: Int, prepare: HttpGetActionBuilder.() -> Unit = {}) =
        HttpGetActionBuilder(port).apply(prepare).let { action = it }

    /**
     * Configures and applies a `TCPSocketAction` for establishing a TCP socket connection.
     *
     * This method allows the setup of a TCP socket probe by specifying the target port
     * and an optional configuration block for further customization.
     *
     * @param port The target port for the TCP socket connection. Must be in the range of 1 to 65535.
     * @param prepare An optional lambda block used to configure a `TCPSocketActionBuilder`. Within this block,
     *                additional settings can be defined for the TCP action.
     */
    fun tcpSocket(port: Int, prepare: TCPSocketActionBuilder.() -> Unit = {}) =
        TCPSocketActionBuilder(port).apply(prepare).let { action = it }

    /**
     * Configures and applies a `GRPCAction` for establishing gRPC communication.
     *
     * This method sets up the behavior of a gRPC probe, specifying the target port
     * and providing an optional configuration block for further customization, such as
     * setting the gRPC service name.
     *
     * @param port The target port for the gRPC communication. Must be in the range of 1 to 65535.
     * @param prepare An optional lambda block to configure the `GRPCActionBuilder`.
     *                Within this block, you can set properties like `service` to
     *                specify the gRPC service name.
     */
    fun grpc(port: Int, prepare: GRPCActionBuilder.() -> Unit = {}) =
        GRPCActionBuilder(port).apply(prepare).let { action = it }

    /**
     * Constructs and returns a configured `ProbeSpec` instance.
     *
     * This method validates the state of required fields before constructing the probe specification.
     * Fields such as `action`, `initialDelaySeconds`, `periodSeconds`, `timeoutSeconds`,
     * `successThreshold`, `failureThreshold`, and `terminationGracePeriodSeconds` are checked
     * for validity (e.g., non-null or non-negative constraints) prior to building the `ProbeSpec`.
     *
     * @return A `ProbeSpec` instance representing the current configuration.
     * @throws IllegalArgumentException if any validation fails.
     */
    internal fun build(): ProbeSpec {
        require(action != null) { "Any action must be set" }
        initialDelaySeconds?.let { require(it >= Duration.ZERO) { "Initial delay must be greater or equals to 0" } }
        periodSeconds?.let { require(it >= Duration.ZERO) { "Period must be greater or equals to 0" } }
        timeoutSeconds?.let { require(it >= Duration.ZERO) { "Timeout must be greater or equals to 0" } }
        successThreshold?.let { require(it >= 0) { "Success threshold must be greater or equals to 0" } }
        failureThreshold?.let { require(it >= 0) { "Failure threshold must be greater or equals to 0" } }
        terminationGracePeriodSeconds?.let { require(it >= Duration.ZERO) { "Termination grace period must be greater or equals to 0" } }

        return ProbeSpec(
            action = action!!.build(),
            initialDelaySeconds = initialDelaySeconds,
            periodSeconds = periodSeconds,
            timeoutSeconds = timeoutSeconds,
            successThreshold = successThreshold,
            failureThreshold = failureThreshold,
            terminationGracePeriodSeconds = terminationGracePeriodSeconds
        )
    }

    /**
     * Represents a builder for constructing implementations of `ProbeAction`.
     *
     * A `ProbeActionBuilder` is used to define the configuration and behavior
     * of a Kubernetes probe action, which can be used to determine the health
     * or readiness of a container within a pod. The builder ensures that the
     * configuration adheres to the constraints required for each specific action type.
     *
     * Implementations of this interface are responsible for providing concrete
     * details on how the probe action is constructed and validated.
     *
     * @param T The specific type of `ProbeAction` that this builder is responsible for creating.
     */
    sealed interface ProbeActionBuilder<T : ProbeAction> {
        /**
         * Constructs and returns an instance of the defined `ProbeAction` type.
         *
         * This method finalizes the configuration and creates an implementation of the probe
         * action based on the options and parameters defined in the builder. The returned
         * instance is suitable for use in Kubernetes configurations requiring a probe action
         * for health or readiness checks.
         *
         * @return A configured instance of the `ProbeAction` type represented by this builder.
         */
        fun build(): T
    }

    /**
     * Builder for configuring and constructing an `ExecAction`.
     *
     * This class facilitates the definition of a Kubernetes exec action,
     * which executes a specified command within a container. Users can configure
     * the command to be executed using the `command(vararg command: String)` method.
     * The constructed action is validated to ensure that a non-empty command is provided.
     *
     * The `ExecAction` created by this builder is used to define probes that execute
     * commands inside a container for health or readiness checks.
     *
     * This class is intended for internal use and should not be instantiated directly.
     * Instances of this builder are typically created and managed by higher-level
     * probe configuration methods.
     *
     * Note: The `command` must be set and should not be empty. If these constraints
     * are not met, an exception will be thrown when attempting to build the action.
     *
     * Implements:
     * - `ProbeActionBuilder<ProbeSpec.ExecAction>`: Provides the capability to construct
     *   an `ExecAction` instance adhering to the general `ProbeAction` framework.
     */
    class ExecActionBuilder internal constructor() : ProbeActionBuilder<ProbeSpec.ExecAction> {
        private var command: MutableList<String>? = null

        /**
         * Adds one or more command strings to the current list of commands.
         * If the command list is not initialized, it will be created.
         *
         * @param command Vararg parameter representing the command(s) to add. Each string in this parameter
         * represents a part of the command to be executed.
         */
        fun command(vararg command: String) {
            if (this.command == null) {
                this.command = mutableListOf()
            }
            this.command!!.addAll(command)
        }

        /**
         * Constructs and returns a new instance of ProbeSpec.ExecAction based on the current state of the builder.
         * Ensures that the required command list is set and not empty before creating the object.
         *
         * @return A new instance of ProbeSpec.ExecAction containing the command list.
         * @throws IllegalArgumentException If the command list is not set or is empty.
         */
        override fun build(): ProbeSpec.ExecAction {
            require(command != null) { "Command must be set" }
            require(command!!.isNotEmpty()) { "Command must not be empty" }

            return ProbeSpec.ExecAction(command ?: emptyList())
        }
    }

    /**
     * A builder class for constructing an `HttpGetAction` used in Kubernetes probes.
     *
     * This builder allows configuring the details of an HTTP GET request, including the target
     * path, host, port, scheme, and headers. The constructed `HttpGetAction` can be used to
     * define readiness or liveness probes for Kubernetes containers.
     *
     * @constructor Creates an instance of `HttpGetActionBuilder` with a specified port.
     * @param port The target port for the HTTP GET request, must be in the range 1 to 65535.
     */
    class HttpGetActionBuilder internal constructor(private val port: Int) : ProbeActionBuilder<ProbeSpec.HttpGetAction> {
        private var httpHeaders: MutableMap<String, String>? = null

        /**
         * Specifies the HTTP request path to be used for the HTTP GET action in a Kubernetes probe.
         *
         * This value represents the relative URI path that will be requested on the target container.
         * If not explicitly set, it defaults to `null`, meaning the root path (`/`) is typically used.
         */
        var path: String? = null

        /**
         * Specifies the host for the HTTP GET action.
         *
         * This variable defines the hostname or IP address that will be targeted by the
         * HTTP GET probe. It is optional and can be left null if the target host should
         * default to the pod's IP address or a different behavior defined elsewhere.
         *
         * Use this property to configure the desired host explicitly for the HTTP request.
         */
        var host: String? = null

        /**
         * Specifies the protocol scheme (HTTP or HTTPS) to be used for the HTTP GET action.
         *
         * This variable allows configuring the communication protocol for the HTTP request,
         * typically used to determine whether the request should be transmitted securely (HTTPS)
         * or without encryption (HTTP). This is particularly relevant in scenarios such as
         * interacting with external APIs, configuring ingress rules, or defining service endpoints.
         *
         * By default, this variable is null, indicating that no scheme is explicitly set.
         */
        var scheme: Scheme? = null

        /**
         * Adds a custom HTTP header to the current set of HTTP headers.
         *
         * If the HTTP headers collection is not initialized, it will be created before
         * adding the specified header.
         *
         * @param name The name of the HTTP header to add. This value cannot be null or empty.
         * @param value The value of the HTTP header to add. This value cannot be null.
         */
        fun addHttpHeader(name: String, value: String) {
            if (httpHeaders == null) {
                httpHeaders = mutableMapOf()
            }
            httpHeaders!![name] = value
        }

        /**
         * Configures HTTP headers using a builder function.
         *
         * This method allows the definition of a set of HTTP headers by providing
         * a DSL-style configuration block. The headers can be customized by the user
         * through the `HttpHeaderMapBuilder`.
         *
         * @param prepare A lambda expression used to build and configure HTTP headers
         *                via the `HttpHeaderMapBuilder` instance. This lambda allows
         *                specifying key-value pairs for HTTP headers.
         */
        fun httpHeaders(prepare: HttpHeaderMapBuilder.() -> Unit) =
            HttpHeaderMapBuilder().apply(prepare)

        /**
         * Builds a `ProbeSpec.HttpGetAction` instance using the configured properties of the builder.
         *
         * Validates that the port is within the valid range (1-65535) before creating the action object.
         *
         * @return A `ProbeSpec.HttpGetAction` instance containing the defined path, port, host, scheme, and HTTP headers.
         * @throws IllegalArgumentException if the port is not within the valid range.
         */
        override fun build() : ProbeSpec.HttpGetAction {
            require(port > 0) { "Port must be greater than 0" }
            require(port <= 65535) { "Port must be less or equals to 65535" }

            return ProbeSpec.HttpGetAction(
                path = path,
                port = port,
                host = host,
                scheme = scheme,
                httpHeaders = httpHeaders
            )
        }

        /**
         * A builder class for constructing a map of HTTP headers.
         *
         * This class provides methods to define HTTP headers as key-value pairs,
         * which can then be used in an HTTP request configuration.
         */
        inner class HttpHeaderMapBuilder {
            /**
             * Adds a new HTTP header to the request header map being constructed.
             *
             * This method allows the specification of a custom HTTP header as a key-value pair.
             * It delegates the operation to the `addHttpHeader` function, which ensures that
             * the headers are properly stored and managed within the builder's internal structure.
             *
             * @param name The name of the HTTP header to add. Must be a non-empty string.
             * @param value The value of the HTTP header to add. Must be a non-null string.
             */
            fun httpHeader(name: String, value: String) =
                addHttpHeader(name, value)
        }
    }

    /**
     * Builder for configuring and creating a `TCPSocketAction`.
     *
     * A `TCPSocketAction` describes a health or readiness probe based on establishing
     * a TCP socket connection to a specific port. This builder validates the port value
     * and prepares the configuration required to define the action.
     *
     * Instances of this builder are primarily used within the `tcpSocket` configuration method
     * provided by higher-level spec builders.
     *
     * @constructor Creates a `TCPSocketActionBuilder` with a given port.
     * @param port The target port for the TCP socket connection. Must be in the range of 1 to 65535.
     *
     * @see ProbeActionBuilder
     * @see ProbeSpec.TCPSocketAction
     */
    class TCPSocketActionBuilder internal constructor(private val port: Int) : ProbeActionBuilder<ProbeSpec.TCPSocketAction> {
        /**
         * Specifies the optional hostname for the TCP socket connection.
         *
         * This field, when defined, determines the target hostname to be used in the TCP socket action.
         * However, its usage is discouraged due to historical inconsistencies in Kubernetes behavior
         * across different versions. The field may not function as expected and is recommended to be avoided
         * in favor of alternative approaches. It may also be removed in future Kubernetes versions.
         *
         * Default is null, indicating no specific hostname is set and the TCP connection will use the
         * default behavior of Kubernetes.
         *
         * @deprecated This field is deprecated due to inconsistent behavior and lack of full implementation
         * in Kubernetes. It is subject to removal in future versions.
         */
        @Deprecated(message = "This field has an unfortunate history in Kubernetes. It was never fully implemented and " +
                "its behavior was inconsistent across different Kubernetes versions. Use of this field is discouraged " +
                "and it may be removed in future versions.")
        var host: String? = null
        /**
         * Builds and returns a `TCPSocketAction` instance with the configured port.
         *
         * This method validates that the port is within the valid range (1 to 65535)
         * before creating the `TCPSocketAction` object. If the port is invalid, an
         * `IllegalArgumentException` will be thrown.
         *
         * @return A `TCPSocketAction` instance configured with the specified port.
         */
        override fun build() : ProbeSpec.TCPSocketAction {
            require(port > 0) { "Port must be greater than 0" }
            require(port <= 65535) { "Port must be less or equals to 65535" }

            return ProbeSpec.TCPSocketAction(
                port = port,
                host = host
            )
        }
    }

    /**
     * A builder class for constructing gRPC probe actions (`GRPCAction`) used in Kubernetes configurations.
     *
     * The `GRPCActionBuilder` allows configuration of a gRPC-based health or readiness probe by specifying
     * the target port and an optional service name.
     *
     * @constructor Initializes the builder with the target port for the gRPC probe.
     * This constructor is internal and should be accessed through higher-level configuration methods.
     *
     * @param port The target port for the gRPC communication. Must be a valid port number between 1 and 65535.
     */
    class GRPCActionBuilder internal constructor(private val port: Int) : ProbeActionBuilder<ProbeSpec.GRPCAction> {
        /**
         * Specifies the name of the gRPC service targeted by the probe action.
         *
         * This property is optional and can be used to define the specific gRPC service
         * when a gRPC probe operates in applications hosting multiple services.
         *
         * If set, the value must not be blank. If left null, the probe will target the
         * default service associated with the gRPC server on the specified port.
         */
        var service: String? = null

        /**
         * Constructs a `ProbeSpec.GRPCAction` instance with the configured port and optional service name.
         *
         * This method validates the provided port to ensure it is within the valid range (1 to 65535). If a service name
         * is specified, it checks that the service name is not blank. These validations ensure that the resulting
         * gRPC action is properly configured for use in Kubernetes probes.
         *
         * @return A `ProbeSpec.GRPCAction` instance representing the configured gRPC probe action with the specified
         *         port and optional service.
         * @throws IllegalArgumentException If the port is invalid or if the service name is blank when specified.
         */
        override fun build() : ProbeSpec.GRPCAction {
            require(port > 0) { "Port must be greater than 0" }
            require(port <= 65535) { "Port must be less or equals to 65535" }
            service?.let { require(it.isNotBlank()) { "Service must not be blank" } }

            return ProbeSpec.GRPCAction(port, service)
        }
    }
}