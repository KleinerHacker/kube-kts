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

import java.time.Duration

/**
 * A builder class for constructing a specification of lifecycle actions (`LifecycleSpec`) 
 * that can be executed during specific phases of a container's lifecycle, such as 
 * `postStart` or `preStop`.
 *
 * The `LifecycleSpecBuilder` allows defining these lifecycle actions through a fluent and 
 * configurable API. Each action phase (`postStart` or `preStop`) can host one or more 
 * specific actions, such as executing a command, performing an HTTP GET, or introducing 
 * a delay in execution.
 *
 * The class is designed for internal construction and usage within lifecycle-related configurations. 
 */
class LifecycleSpecBuilder internal constructor() {
    private var postStart: CommonActionBuilder? = null
    private var preStop: CommonActionBuilder? = null

    /**
     * Configures the actions to be executed after the start of the lifecycle.
     *
     * This method allows defining a set of actions using a builder pattern. 
     * The provided lambda is used to configure the `CommonActionBuilder`, 
     * which determines the actions that will be executed post start.
     *
     * @param prepare A lambda expression to configure the `CommonActionBuilder` for post-start execution.
     */
    fun postStart(prepare: CommonActionBuilder.() -> Unit) {
        postStart = CommonActionBuilder().apply(prepare)
    }

    /**
     * Configures the actions to be executed before the termination of the lifecycle.
     *
     * This method enables the definition of a set of actions performed before the
     * lifecycle enters the termination phase. The provided lambda is used to 
     * configure an instance of `CommonActionBuilder`, allowing for customization
     * of pre-termination behavior.
     *
     * @param prepare A lambda expression to configure the `CommonActionBuilder` for pre-termination execution.
     */
    fun preStop(prepare: CommonActionBuilder.() -> Unit) {
        preStop = CommonActionBuilder().apply(prepare)
    }

    /**
     * Builds and returns a `LifecycleSpec` instance based on the currently configured
     * `postStart` and `preStop` actions in the `LifecycleSpecBuilder`.
     *
     * This method finalizes the lifecycle configuration by aggregating the actions defined
     * for post-start and pre-stop phases. If no actions are configured for a particular phase,
     * the corresponding property in the resulting `LifecycleSpec` will be `null`.
     *
     * @return An instance of `LifecycleSpec` containing the configured `postStart` and `preStop` actions,
     * or `null` for each phase where no actions were defined.
     */
    internal fun build(): LifecycleSpec = LifecycleSpec(postStart?.build(), preStop?.build())

    /**
     * A builder class for defining and configuring common actions executed at specific points
     * in a lifecycle. This class provides APIs to define actions such as executing commands,
     * performing HTTP GET requests, or introducing delays.
     *
     * The `CommonActionBuilder` is typically used within the context of lifecycle
     * configuration to define actions that need to be executed as part of a `LifecycleSpec`.
     */
    class CommonActionBuilder internal constructor() {
        private var action: ActionBuilder<*>? = null

        /**
         * Defines an execution action using the provided preparation block.
         *
         * This method allows configuring an `ExecActionBuilder` instance to define a command
         * to be executed as part of a lifecycle or other execution context.
         *
         * @param prepare A lambda function used to configure the `ExecActionBuilder`. Within this lambda,
         * you can define the command to execute by calling the `command` method.
         */
        fun exec(prepare: ExecActionBuilder.() -> Unit) {
            action = ExecActionBuilder().apply(prepare)
        }

        /**
         * Configures an HTTP GET action to be executed during a specific lifecycle event. The action can be
         * customized using the provided preparation block.
         *
         * @param port The port number on which the HTTP GET request should be made.
         * @param prepare A lambda function used to configure the HTTP GET action using the 
         *                `HttpGetActionBuilder`. Within this block, properties such as the path, host,
         *                scheme, and headers for the HTTP GET request can be specified.
         */
        fun httpGet(port: Int, prepare: HttpGetActionBuilder.() -> Unit = {}) {
            action = HttpGetActionBuilder(port).apply(prepare)
        }

        /**
         * Configures a sleep action with the specified duration. This action introduces a delay
         * in the execution flow for the given number of seconds.
         *
         * @param seconds The duration for which the sleep action should hold execution. Must be positive.
         */
        fun sleep(seconds: Duration, prepare: SleepActionBuilder.() -> Unit = {}) {
            action = SleepActionBuilder(seconds).apply(prepare)
        }

        /**
         * Builds and returns a `LifecycleSpec.Action` instance based on the current configuration.
         *
         * This method finalizes the construction of an action, if it has been previously defined
         * through the builder's configuration methods. If no action has been defined, the method
         * returns null.
         *
         * @return A constructed `LifecycleSpec.Action` instance or null if no action is specified.
         */
        internal fun build(): LifecycleSpec.Action? = action?.build()
    }

    /**
     * Represents a builder for defining lifecycle actions for containers.
     *
     * This interface allows the construction of lifecycle actions of type `T`, 
     * which are used to define custom behaviors that a container should execute 
     * during specific lifecycle phases such as `postStart` or `preStop`.
     *
     * @param T The type of action being built, constrained to implementations of `LifecycleSpec.Action`.
     */
    sealed interface ActionBuilder<T : LifecycleSpec.Action> {
        /**
         * Constructs and returns the lifecycle action object of type `T`.
         *
         * This method finalizes the configuration within the builder 
         * and creates an instance of the action that can be used 
         * as part of the container's lifecycle phase, such as postStart or preStop.
         *
         * @return The built lifecycle action instance of type `T`.
         */
        fun build(): T
    }

    /**
     * A builder for constructing an `ExecAction` instance, which represents
     * a command to be executed within a container as part of its lifecycle configuration.
     *
     * This class provides a fluent API for defining the command to be executed. The configured
     * `ExecAction` can be used in lifecycle hooks such as `postStart` or `preStop` within a container.
     *
     * This builder processes the user-provided command and constrains it to non-empty
     * values before producing a final `ExecAction` object.
     *
     * This class is designed for internal use and should not be instantiated directly.
     * Instances are typically created and managed by the enclosing class, such as a
     * `LifecycleSpecBuilder`.
     *
     * @constructor Internal constructor to prevent direct instantiation.
     */
    class ExecActionBuilder internal constructor() : ActionBuilder<LifecycleSpec.ExecAction> {
        private var command: MutableList<String>? = null

        /**
         * Adds one or more command strings to the current command list.
         *
         * This method appends the provided command elements to the internal command
         * list, initializing it if necessary. These commands define the executable
         * and its arguments for container lifecycle hooks.
         *
         * @param command One or more command strings to be added. Each string represents
         * an executable or its argument in the command sequence. Must not be null or empty.
         */
        fun command(vararg command: String) {
            if (this.command == null) {
                this.command = mutableListOf()
            }
            this.command!!.addAll(command)
        }

        /**
         * Builds and returns a new instance of `LifecycleSpec.ExecAction` based on the current state of the builder.
         *
         * The `ExecAction` object encapsulates a list of commands to be executed as part of a container's lifecycle events,
         * such as `postStart` or `preStop`. The method ensures that the command list is properly initialized and is not empty
         * before creating the `ExecAction` instance.
         *
         * @return A new `LifecycleSpec.ExecAction` object containing the command list initialized in the builder.
         * @throws IllegalStateException if the command list is null or empty.
         */
        override fun build(): LifecycleSpec.ExecAction {
            require(command != null) { "Command must be set" }
            require(command!!.isNotEmpty()) { "Command must not be empty" }
            
            return LifecycleSpec.ExecAction(command = command ?: emptyList())
        }
    }

    /**
     * Builds an HTTP GET action for a container's lifecycle configuration.
     *
     * This builder is used to define an HTTP GET request that can be executed as part of a container's 
     * lifecycle, such as during the `postStart` or `preStop` events.
     *
     * @constructor Creates an instance of the builder with the specified target port.
     * @param port The port on the target host to which the GET request will be sent.
     */
    class HttpGetActionBuilder internal constructor(private val port: Int) : ActionBuilder<LifecycleSpec.HttpGetAction> {
        private var httpHeaders: MutableMap<String, String>? = null

        /**
         * The path component of the HTTP GET request.
         *
         * This defines the relative URI path that will be appended to the host and port
         * when constructing the full HTTP GET request for a container's lifecycle event.
         * For example, a path value of `/healthz` can be used to target health check endpoints.
         *
         * This property can be null, indicating that no specific path is defined, in which
         * case only the host and port will be used for the request.
         */
        var path: String? = null

        /**
         * Specifies the host for the HTTP GET action.
         *
         * The `host` variable represents the target hostname that the HTTP GET request 
         * will be sent to. This can either be a domain name or an IP address. If set to `null`,
         * the host value may fall back to a default value or be determined by the containing 
         * class's logic.
         *
         * This property is optional and can be used to customize the target of the HTTP 
         * GET request based on specific requirements.
         */
        var host: String? = null

        /**
         * Defines the protocol scheme to be used for HTTP requests in the action builder.
         *
         * This variable allows specifying whether the requests should use an unsecured
         * protocol (HTTP) or a secured protocol (HTTPS). The scheme is critical in
         * determining the security level of the communication and how the URLs are
         * constructed during the building of an HTTP GET action.
         *
         * Defaults to `null`, indicating that the scheme has not been explicitly set.
         */
        var scheme: ProtocolScheme? = null

        /**
         * Adds an HTTP header to the request.
         *
         * This method allows adding a custom header to the HTTP request by specifying
         * the header name and its corresponding value. If the headers map is not initialized,
         * it creates a new mutable map to store the headers and then assigns the provided
         * name-value pair to the map.
         *
         * @param name The name of the HTTP header to be added.
         * @param value The value of the HTTP header to be added.
         */
        fun addHttpHeader(name: String, value: String) {
            if (httpHeaders == null) {
                httpHeaders = mutableMapOf()
            }
            httpHeaders!![name] = value
        }

        /**
         * Configures the HTTP headers for a request using a provided configuration block.
         *
         * This method allows you to define custom HTTP headers that will be included 
         * in the constructed request. A block of type `HttpHeadersBuilder.() -> Unit` 
         * is used to customize the headers.
         *
         * Example usage:
         * ```kotlin
         * httpHeaders {
         *     httpHeader("Content-Type", "application/json")
         *     httpHeader("Authorization", "Bearer token123")
         * }
         * ```
         *
         * @param prepare A lambda function with a receiver of type `HttpHeadersBuilder` 
         *                used to specify the HTTP headers configuration.
         */
        fun httpHeaders(prepare: HttpHeadersBuilder.() -> Unit) {
            HttpHeadersBuilder().apply(prepare)
        }

        /**
         * Constructs and returns an instance of `LifecycleSpec.HttpGetAction` based on the current configuration.
         *
         * This method takes the configured `path`, `port`, `host`, `scheme`, and `httpHeaders` fields
         * of the `HttpGetActionBuilder` and builds an `HttpGetAction` object to represent an HTTP GET
         * request for container lifecycle events.
         *
         * @return A `LifecycleSpec.HttpGetAction` instance created from the current builder configuration.
         */
        override fun build(): LifecycleSpec.HttpGetAction {
            return LifecycleSpec.HttpGetAction(
                path = path,
                port = port,
                host = host,
                scheme = scheme,
                httpHeaders = httpHeaders
            )
        }

        /**
         * A builder class for constructing HTTP headers to be included in an HTTP request.
         *
         * This class provides methods for defining key-value pairs representing HTTP headers.
         * It is used in conjunction with `HttpGetActionBuilder` to customize HTTP requests
         * by setting desired headers.
         *
         * @constructor Internal constructor, use `httpHeaders` method from `HttpGetActionBuilder`
         *              to create and configure instances of `HttpHeadersBuilder`.
         */
        inner class HttpHeadersBuilder internal constructor() {
            /**
             * Adds an HTTP header to the request being constructed.
             *
             * This method appends a specific HTTP header, represented as a name-value pair,
             * to the ongoing configuration of the HTTP request.
             *
             * Example usage:
             * ```kotlin
             * httpHeaders {
             *     httpHeader("Content-Type", "application/json")
             *     httpHeader("Authorization", "Bearer token123")
             * }
             * ```
             *
             * @param name The name of the HTTP header to be added. Must not be null or blank.
             * @param value The value of the HTTP header to be added. Can be blank if the header does not require a value.
             */
            fun httpHeader(name: String, value: String) =
                addHttpHeader(name, value)
        }
    }

    /**
     * A builder for creating instances of `LifecycleSpec.SleepAction`, which introduces a delay
     * during a container's lifecycle event.
     *
     * This builder is used to define a pause action with a specified duration in seconds.
     * The resulting `SleepAction` can be added to the `postStart` or `preStop` phase of
     * a container's lifecycle configuration.
     *
     * @constructor Creates a new `SleepActionBuilder` with the specified duration.
     * @param seconds The duration of the sleep action, represented as a `Duration` object.
     *                Must be a positive value.
     */
    class SleepActionBuilder(private val seconds: Duration) : ActionBuilder<LifecycleSpec.SleepAction> {
        /**
         * Builds and returns a `LifecycleSpec.SleepAction` instance based on the specified duration.
         *
         * This method validates that the duration is positive and then constructs a `SleepAction`
         * object, which represents a pause action during a container's lifecycle event.
         *
         * @return A `LifecycleSpec.SleepAction` configured with the specified sleep duration.
         * @throws IllegalArgumentException if the specified duration is not positive.
         */
        override fun build(): LifecycleSpec.SleepAction {
            require(seconds.isPositive) { "Seconds must be positive" }
            
            return LifecycleSpec.SleepAction(seconds)
        }
    }
    
}