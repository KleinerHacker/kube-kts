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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.ContainerSpec.ImagePullPolicy
import org.pcsoft.framework.kube.kts.api.chart.resources.types.ContainerSpec.TerminationMessagePolicy

/**
 * Builder class for defining and configuring container specifications.
 *
 * The `ContainerSpecBuilder` is used to create and configure container definitions in a fluent manner.
 * It provides methods to define container attributes such as ports, environment variables, resource limits,
 * volume mounts, lifecycle configurations, and security contexts.
 *
 * This builder is intended to construct specifications for containerized applications, including 
 * their associated runtime configurations and deployment behaviors.
 * 
 * @constructor
 * Creates a new instance of `ContainerSpecBuilder` with a required container name and image.
 * This constructor is marked as internal, meaning it is not directly accessible outside of the defined scope.
 *
 * @property name The name of the container. Acts as an identifier within the configuration.
 * @property image The container image to be used. Defines the application or runtime environment for the container.
 */
class ContainerSpecBuilder internal constructor(private var name: String, private var image: String) {
    private var ports: MutableList<PortSpecBuilder>? = null
    private var env: SingleEnvironmentSpecBuilder? = null
    private var envFrom: CompleteEnvironmentSpecBuilder? = null
    private var resources: HardwareResourceSpecBuilder? = null
    private var volumeMounts: MutableList<VolumeMountSpecBuilder>? = null
    private var volumeDevices: MutableList<VolumeDeviceSpecBuilder>? = null
    private var livenessProbe: ProbeSpecBuilder? = null
    private var readinessProbe: ProbeSpecBuilder? = null
    private var startupProbe: ProbeSpecBuilder? = null
    private var lifecycle: LifecycleSpecBuilder? = null
    private var securityContext: SecurityContextSpecBuilder? = null
    private var command: MutableList<String>? = null
    private var args: MutableList<String>? = null

    /**
     * Defines the image pull policy for the container in the pod specification.
     *
     * This property specifies how the container image is retrieved from the container registry
     * during pod creation or updates. The value can control whether the image is always pulled,
     * pulled only if not already present locally, or never pulled.
     *
     * If not explicitly set, the behavior defaults to Kubernetes' default image pull policy,
     * typically determined by the image tag (e.g., `Always` for `:latest` tags and `IfNotPresent` for others).
     */
    var imagePullPolicy: ImagePullPolicy? = null

    /**
     * Specifies the file path for the termination message of a container.
     *
     * The termination message path is used to specify a file where a container can write
     * termination-related information. Kubernetes reads this file upon the container's
     * termination and makes the contents available in the corresponding Pod status.
     *
     * If not specified, Kubernetes uses a default path to retrieve the termination message.
     * Configuring this allows users to customize the location of the termination message file.
     */
    var terminationMessagePath: String? = null

    /**
     * Specifies the policy for capturing termination messages for the container.
     *
     * Termination messages provide details about a container's termination status,
     * including error messages and other relevant information.
     *
     * The policy determines how the container captures and exposes the termination message.
     *
     * This property is optional and can be set to null if termination message behavior
     * is not explicitly required.
     */
    var terminationMessagePolicy: TerminationMessagePolicy? = null

    /**
     * Indicates whether the container's standard input (stdin) stream should be kept open.
     *
     * This property is used to determine if a container should allocate a terminal 
     * and keep the standard input open for user interaction or other purposes. 
     * It is especially useful when interactive sessions or command input are required 
     * within the container.
     *
     * A value of `true` ensures that stdin remains open for the container, 
     * while `false` or `null` indicates that stdin will not be kept open.
     */
    var stdin: Boolean? = null

    /**
     * Specifies whether the container's standard input (stdin) stream should be opened only once.
     *
     * When set to `true`, the container's stdin will be opened once and then closed after the first
     * interaction. If set to `false` or `null`, the behavior will depend on the default configuration
     * or external context.
     */
    var stdinOnce: Boolean? = null

    /**
     * Indicates whether a terminal is allocated for the container.
     *
     * When set to `true`, the container will be allocated a pseudo-TTY (teletypewriter),
     * which can be used for interactive commands or troubleshooting. If set to `false`,
     * no terminal will be allocated. A value of `null` indicates that this property
     * has not been explicitly configured.
     */
    var tty: Boolean? = null

    /**
     * Specifies the working directory for the container.
     *
     * This property defines the directory within the container's filesystem
     * where the process will start its execution. If not explicitly set, the
     * container runtime may use a platform-specific default directory.
     *
     * A working directory can be useful for scenarios where commands or
     * applications need to execute relative to a specific directory structure.
     *
     * The value should be an absolute path on the container's filesystem, or
     * it can remain null to indicate no specific working directory is configured.
     */
    var workingDir: String? = null

    /**
     * Adds a new port specification to the container with the specified container port and configuration.
     *
     * This method allows defining a container port and applying additional configurations using a `PortSpecBuilder`.
     *
     * Example usage:
     * ```kotlin
     * addPort(8080) {
     *     name = "http"
     *     protocol = Protocol.TCP
     * }
     * ```
     *
     * @param containerPort The port number inside the container to which the configuration will be applied.
     * @param prepare A lambda receiver to configure the port using the `PortSpecBuilder`.
     */
    fun addPort(containerPort: Int, prepare: PortSpecBuilder.() -> Unit = {}) {
        if (ports == null) {
            ports = mutableListOf()
        }
        ports!!.add(PortSpecBuilder(containerPort).apply(prepare))
    }

    /**
     * Configures the ports for the container by defining a list of port specifications.
     *
     * This method allows you to create and configure multiple port specifications for a container
     * using the provided `PortSpecListBuilder`. Each port specification can be defined with its
     * container port and additional properties such as protocol, name, etc.
     *
     * Example usage:
     * ```kotlin
     * ports {
     *     port(8080) {
     *         name = "http"
     *         protocol = Protocol.TCP
     *     }
     *     port(8443) {
     *         name = "https"
     *         protocol = Protocol.TCP
     *     }
     * }
     * ```
     *
     * @param prepare A lambda receiver to define and configure a list of ports using the `PortSpecListBuilder`.
     */
    fun ports(prepare: PortSpecListBuilder.() -> Unit = {}) =
        PortSpecListBuilder().apply(prepare)

    /**
     * Defines an environment variable for the container and allows configuring its specification.
     *
     * This method enables the creation of an environment variable by specifying a name
     * and applying further configuration using the `SingleEnvironmentSpecBuilder`.
     *
     * Example usage:
     * ```kotlin
     * env("DATABASE_URL") {
     *     value = "jdbc:postgresql://localhost:5432/mydb"
     * }
     * ```
     *
     * @param name The name of the environment variable to define.
     * @param prepare A lambda receiver to configure the environment variable using the `SingleEnvironmentSpecBuilder`.
     */
    fun env(name: String, prepare: SingleEnvironmentSpecBuilder.() -> Unit) {
        env = SingleEnvironmentSpecBuilder(name).apply(prepare)
    }

    /**
     * Configures a container's environment variables from an external source or resource.
     *
     * This method allows specifying environment variables for the container using a builder
     * that facilitates creating and configuring multiple environment variable specifications.
     *
     * Example usage:
     * ```kotlin
     * envFrom {
     *     prefix = "APP_"
     *     configMapRef("app-config") {
     *         optional = true
     *     }
     * }
     * ```
     *
     * @param prepare A lambda receiver to define and configure the environment variables using the `CompleteEnvironmentSpecBuilder`.
     */
    fun envFrom(prepare: CompleteEnvironmentSpecBuilder.() -> Unit) {
        envFrom = CompleteEnvironmentSpecBuilder().apply(prepare)
    }

    /**
     * Configures the hardware resource requirements for the container.
     *
     * This method allows defining resource requests and limits such as CPU and memory
     * using the `HardwareResourceSpecBuilder`.
     *
     * Example usage:
     * ```kotlin
     * resources {
     *     requests {
     *         cpu = "100m"
     *         memory = "128Mi"
     *     }
     *     limits {
     *         cpu = "500m"
     *         memory = "512Mi"
     *     }
     * }
     * ```
     *
     * @param prepare A lambda receiver to configure the hardware resources using the `HardwareResourceSpecBuilder`.
     */
    fun resources(prepare: HardwareResourceSpecBuilder.() -> Unit) {
        resources = HardwareResourceSpecBuilder().apply(prepare)
    }

    /**
     * Adds a volume mount specification to the container.
     *
     * This method defines how a volume is mounted within the container's filesystem
     * by specifying the volume name and its mount path.
     *
     * Example usage:
     * ```kotlin
     * addVolumeMount("data-volume", "/var/data") {
     *     readOnly = true
     *     subPath = "logs"
     * }
     * ```
     *
     * @param name The name of the volume to mount.
     * @param mountPath The path inside the container where the volume will be mounted.
     * @param prepare A lambda receiver to configure additional properties using the `VolumeMountSpecBuilder`.
     */
    fun addVolumeMount(name: String, mountPath: String, prepare: VolumeMountSpecBuilder.() -> Unit = {}) {
        if (volumeMounts == null) {
            volumeMounts = mutableListOf()
        }
        volumeMounts!!.add(VolumeMountSpecBuilder(name, mountPath).apply(prepare))
    }

    /**
     * Configures multiple volume mount specifications for the container.
     *
     * This method allows defining a list of volume mounts using the `VolumeMountSpecListBuilder`.
     *
     * Example usage:
     * ```kotlin
     * volumeMounts {
     *     volumeMount("config-volume", "/etc/config") {
     *         readOnly = true
     *     }
     *     volumeMount("data-volume", "/var/data")
     * }
     * ```
     *
     * @param prepare A lambda receiver to define multiple volume mounts using the `VolumeMountSpecListBuilder`.
     */
    fun volumeMounts(prepare: VolumeMountSpecListBuilder.() -> Unit) =
        VolumeMountSpecListBuilder().apply(prepare)

    /**
     * Adds a volume device specification to the container.
     *
     * This method defines a block device that will be accessible within the container
     * by specifying the device name and its path.
     *
     * @param name The name of the volume device.
     * @param devicePath The path inside the container where the device will be accessible.
     * @param prepare A lambda receiver to configure additional properties using the `VolumeDeviceSpecBuilder`.
     */
    fun addVolumeDevice(name: String, devicePath: String, prepare: VolumeDeviceSpecBuilder.() -> Unit = {}) {
        if (volumeDevices == null) {
            volumeDevices = mutableListOf()
        }
        volumeDevices!!.add(VolumeDeviceSpecBuilder(name, devicePath).apply(prepare))
    }

    /**
     * Configures multiple volume device specifications for the container.
     *
     * This method allows defining a list of volume devices using the `VolumeDeviceSpecListBuilder`.
     *
     * Example usage:
     * ```kotlin
     * volumeDevices {
     *     volumeDevice("ssd1", "/dev/xvda")
     *     volumeDevice("ssd2", "/dev/xvdb")
     * }
     * ```
     *
     * @param prepare A lambda receiver to define multiple volume devices using the `VolumeDeviceSpecListBuilder`.
     */
    fun volumeDevices(prepare: VolumeDeviceSpecListBuilder.() -> Unit) =
        VolumeDeviceSpecListBuilder().apply(prepare)

    /**
     * Configures the liveness probe for the container.
     *
     * This method defines a probe used to determine if the container is running.
     * If the liveness probe fails, the container will be restarted.
     *
     * Example usage:
     * ```kotlin
     * livenessProbe {
     *     httpGet {
     *         path = "/healthz"
     *         port = 8080
     *     }
     *     initialDelaySeconds = 30
     *     periodSeconds = 10
     * }
     * ```
     *
     * @param prepare A lambda receiver to configure the liveness probe using the `ProbeSpecBuilder`.
     */
    fun livenessProbe(prepare: ProbeSpecBuilder.() -> Unit) {
        livenessProbe = ProbeSpecBuilder().apply(prepare)
    }

    /**
     * Configures the readiness probe for the container.
     *
     * This method defines a probe used to determine if the container is ready to accept traffic.
     * If the readiness probe fails, the container will be removed from service endpoints.
     *
     * Example usage:
     * ```kotlin
     * readinessProbe {
     *     tcpSocket {
     *         port = 8080
     *     }
     *     initialDelaySeconds = 5
     *     periodSeconds = 5
     * }
     * ```
     *
     * @param prepare A lambda receiver to configure the readiness probe using the `ProbeSpecBuilder`.
     */
    fun readinessProbe(prepare: ProbeSpecBuilder.() -> Unit) {
        readinessProbe = ProbeSpecBuilder().apply(prepare)
    }

    /**
     * Configures the startup probe for the container.
     *
     * This method defines a probe used to determine if the application within the container has started.
     * All other probes are disabled until the startup probe succeeds.
     *
     * Example usage:
     * ```kotlin
     * startupProbe {
     *     exec {
     *         command("/bin/sh", "-c", "test -f /tmp/started")
     *     }
     *     failureThreshold = 30
     *     periodSeconds = 10
     * }
     * ```
     *
     * @param prepare A lambda receiver to configure the startup probe using the `ProbeSpecBuilder`.
     */
    fun startupProbe(prepare: ProbeSpecBuilder.() -> Unit) {
        startupProbe = ProbeSpecBuilder().apply(prepare)
    }

    /**
     * Configures lifecycle hooks for the container.
     *
     * This method allows defining actions that should be executed at specific points
     * in the container's lifecycle, such as after start or before stop.
     *
     * Example usage:
     * ```kotlin
     * lifecycle {
     *     postStart {
     *         exec {
     *             command("/bin/sh", "-c", "echo 'Container started'")
     *         }
     *     }
     *     preStop {
     *         httpGet {
     *             path = "/shutdown"
     *             port = 8080
     *         }
     *     }
     * }
     * ```
     *
     * @param prepare A lambda receiver to configure the lifecycle hooks using the `LifecycleSpecBuilder`.
     */
    fun lifecycle(prepare: LifecycleSpecBuilder.() -> Unit) {
        lifecycle = LifecycleSpecBuilder().apply(prepare)
    }

    /**
     * Configures the security context for the container.
     *
     * This method allows defining security settings such as running as a specific user,
     * privilege escalation, capabilities, and SELinux options.
     *
     * Example usage:
     * ```kotlin
     * securityContext {
     *     runAsUser = 1000
     *     runAsNonRoot = true
     *     readOnlyRootFilesystem = true
     *     allowPrivilegeEscalation = false
     * }
     * ```
     *
     * @param prepare A lambda receiver to configure the security context using the `SecurityContextSpecBuilder`.
     */
    fun securityContext(prepare: SecurityContextSpecBuilder.() -> Unit) {
        securityContext = SecurityContextSpecBuilder().apply(prepare)
    }

    /**
     * Defines the command to run in the container.
     *
     * This method sets the entrypoint for the container, overriding the default
     * command specified in the container image.
     *
     * Example usage:
     * ```kotlin
     * command("/bin/sh", "-c", "echo 'Hello World'")
     * ```
     *
     * @param command Variable number of command arguments to execute.
     */
    fun command(vararg command: String) {
        if (this.command == null) {
            this.command = mutableListOf()
        }
        this.command!!.addAll(command)
    }

    /**
     * Defines the arguments for the container's command.
     *
     * This method sets the arguments passed to the container's entrypoint,
     * overriding the default arguments specified in the container image.
     *
     * Example usage:
     * ```kotlin
     * args("--port=8080", "--debug")
     * ```
     *
     * @param args Variable number of arguments to pass to the container command.
     */
    fun args(vararg args: String) {
        if (this.args == null) {
            this.args = mutableListOf()
        }
        this.args!!.addAll(args)   
    }

    /**
     * Builds a `ContainerSpec` object using the current configuration of this builder.
     *
     * @return A `ContainerSpec` instance containing all configured container properties.
     */
    internal fun build() = ContainerSpec(
        name = name,
        image = image,
        imagePullPolicy = imagePullPolicy,
        ports = ports?.map { it.build() },
        env = env?.build(),
        envFrom = envFrom?.build(),
        resources = resources?.build(),
        volumeMounts = volumeMounts?.map { it.build() },
        volumeDevices = volumeDevices?.map { it.build() },
        livenessProbe = livenessProbe?.build(),
        readinessProbe = readinessProbe?.build(),
        startupProbe = startupProbe?.build(),
        lifecycle = lifecycle?.build(),
        terminationMessagePath = terminationMessagePath,
        terminationMessagePolicy = terminationMessagePolicy,
        stdin = stdin,
        stdinOnce = stdinOnce,
        tty = tty,
        securityContext = securityContext?.build(),
        command = command,
        args = args,
        workingDir = workingDir,
    )

    /**
     * Builder class for defining and configuring a single port specification for a container.
     *
     * A `PortSpecBuilder` is used to create and configure mappings for container ports, including
     * assigning a specific protocol (e.g., TCP, UDP, or SCTP) and an optional name for identification.
     *
     * @constructor
     * Initializes a new instance of `PortSpecBuilder` with a required container port.
     * This constructor is meant for internal use only and is not exposed to the general API surface.
     *
     * @property containerPort The port number within the container where the port mapping will be applied.
     */
    class PortSpecBuilder internal constructor(private val containerPort: Int) {
        /**
         * The optional name assigned to the port within the container.
         *
         * This name is used as an identifier for the port specification, allowing other components 
         * or configurations to refer to this port by name rather than its number or protocol. 
         * It can be null if no specific name is defined.
         */
        var name: String? = null

        /**
         * Specifies the transport layer protocol used for the port configuration.
         *
         * The protocol determines the type of communication (e.g., TCP, UDP, SCTP)
         * between containerized applications, ensuring compatibility with the defined
         * networking requirements. If not specified, the protocol can default to a
         * predefined value depending on the container orchestration platform.
         */
        var protocol: Protocol? = null

        /**
         * Builds a `ContainerSpec.PortSpec` instance using the configured values.
         *
         * This method creates a new immutable port specification object for the container, 
         * encapsulating the port's name, number, and protocol. It is used to finalize the 
         * configuration defined within the builder.
         *
         * @return An instance of `ContainerSpec.PortSpec` containing the port specification details.
         */
        internal fun build(): ContainerSpec.PortSpec = ContainerSpec.PortSpec(name, containerPort, protocol)
    }

    /**
     * Builder class for managing a list of container port specifications.
     *
     * The `PortSpecListBuilder` is responsible for defining and configuring multiple port
     * specifications for a container. It allows adding ports by specifying their container
     * port numbers and applying additional configuration through the `PortSpecBuilder`.
     *
     * This class is intended for internal usage within the `ContainerSpecBuilder` to
     * streamline the creation of port configurations during container setup.
     */
    inner class PortSpecListBuilder internal constructor() {
        /**
         * Adds a new port specification to the list of container ports.
         *
         * This method allows specifying the container port number and additional configurations
         * for the port through a builder function. The port configuration can include properties 
         * such as protocol, name, and other attributes relevant to the container's port mapping.
         *
         * @param containerPort The port number inside the container that will be configured.
         * @param prepare A lambda function to configure the properties of the port using the `PortSpecBuilder`.
         */
        fun port(containerPort: Int, prepare: PortSpecBuilder.() -> Unit) = 
            addPort(containerPort, prepare)
    }

    /**
     * Builder class for managing and configuring a list of volume mount specifications for a container.
     *
     * This class provides a fluent interface for defining multiple volume mounts within a container
     * configuration. Each volume mount specifies how a volume is mounted within the container's
     * filesystem, including details such as the volume name and its mount path.
     *
     * This builder's main purpose is to organize multiple `VolumeMountSpec` instances and offer
     * a convenient way to add new specifications using a dedicated sub-builder.
     *
     * @constructor Initializes an empty configuration for the volume mount list.
     *
     * @see VolumeMountSpecBuilder
     * @see ContainerSpecBuilder
     */
    inner class VolumeMountSpecListBuilder internal constructor() {
        /**
         * Adds a new volume mount specification to the current configuration.
         *
         * This function defines how a volume will be mounted inside the container's filesystem.
         * A volume mount is specified by the volume name, the mount path within the container, 
         * and optional configurations that can be set using the `prepare` lambda.
         *
         * @param name The name of the volume to be mounted.
         * @param mountPath The path inside the container where the volume should be mounted.
         * @param prepare A lambda function to configure additional properties of the volume mount
         *                using the `VolumeMountSpecBuilder`.
         */
        fun volumeMount(name: String, mountPath: String, prepare: VolumeMountSpecBuilder.() -> Unit = {}) =
            addVolumeMount(name, mountPath, prepare)   
    }

    /**
     * Builder class for managing a list of volume device specifications within a container.
     *
     * This builder allows adding and configuring multiple `VolumeDeviceSpec` instances,
     * each of which defines the mapping of a block device to a path inside the container.
     * The `VolumeDeviceSpecListBuilder` is typically used to simplify the creation
     * of volume device specifications by providing a declarative DSL.
     *
     * Instances of this class are intended for internal use by the container specification
     * builder and should not be created directly.
     */
    inner class VolumeDeviceSpecListBuilder internal constructor() {
        /**
         * Adds a volume device to the current configuration.
         *
         * This method allows you to define a volume device and its associated path within the container.
         * A volume device represents a mapping of a block storage device to a specific path inside the container.
         *
         * @param name The name of the volume device.
         * @param devicePath The path inside the container where the volume device will be accessible.
         * @param prepare A lambda function for configuring additional properties of the volume device using a `VolumeDeviceSpecBuilder`.
         */
        fun volumeDevice(name: String, devicePath: String, prepare: VolumeDeviceSpecBuilder.() -> Unit = {}) =
            addVolumeDevice(name, devicePath, prepare)  
    }
}