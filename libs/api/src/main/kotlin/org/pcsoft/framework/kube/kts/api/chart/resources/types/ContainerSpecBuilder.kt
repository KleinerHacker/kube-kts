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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.ContainerSpec.*
import org.pcsoft.framework.kube.kts.api.chart.resources.types.ResourceResizePolicySpec.ResourceName

/**
 * Builder for a [ContainerSpec], describing a single container of a Pod.
 *
 * Collection-valued settings accumulate: calling [env], [addPort] or [addVolumeMount] several times adds
 * several entries rather than replacing the previous one. Each of them also has a list form - [envs],
 * [ports], [volumeMounts] - that groups multiple entries in one block.
 *
 * @constructor Creates a builder for a container with the given name and image.
 * @param name  The name of the container. Must be unique within the Pod.
 * @param image The container image to run.
 */
class ContainerSpecBuilder internal constructor(private var name: String, private var image: String) {
    private var ports: MutableList<ContainerPortSpecBuilder>? = null
    private var env: MutableList<SingleEnvironmentSpecBuilder>? = null
    private var envFrom: MutableList<CompleteEnvironmentSpecBuilder>? = null
    private var resources: HardwareResourceSpecBuilder? = null
    private var resizePolicy: MutableList<ResourceResizePolicySpecBuilder>? = null
    private var volumeMounts: MutableList<VolumeMountSpecBuilder>? = null
    private var volumeDevices: MutableList<VolumeDeviceSpecBuilder>? = null
    private var livenessProbe: ProbeSpecBuilder? = null
    private var readinessProbe: ProbeSpecBuilder? = null
    private var startupProbe: ProbeSpecBuilder? = null
    private var lifecycle: LifecycleSpecBuilder? = null
    private var securityContext: ContainerSecurityContextSpecBuilder? = null
    private var command: MutableList<String>? = null
    private var args: MutableList<String>? = null

    /**
     * Controls when the container image is pulled from the registry.
     *
     * If unset, Kubernetes derives the policy from the image tag: `Always` for `:latest`, `IfNotPresent`
     * otherwise.
     */
    var imagePullPolicy: ImagePullPolicy? = null

    /**
     * Overrides the Pod's restart policy for this container.
     *
     * Setting [RestartPolicy.Always] on an init container turns it into a native sidecar that keeps
     * running alongside the Pod's main containers.
     */
    var restartPolicy: RestartPolicy? = null

    /**
     * The file the container writes its termination message to.
     *
     * Kubernetes reads this file when the container exits and surfaces its content in the Pod status.
     */
    var terminationMessagePath: String? = null

    /**
     * Controls how the termination message is derived.
     */
    var terminationMessagePolicy: TerminationMessagePolicy? = null

    /**
     * If true, allocates a buffer for the container's standard input.
     */
    var stdin: Boolean? = null

    /**
     * If true, standard input is closed once the first attached session ends.
     */
    var stdinOnce: Boolean? = null

    /**
     * If true, allocates a pseudo-TTY for the container.
     */
    var tty: Boolean? = null

    /**
     * The working directory the entrypoint is executed in. Uses the image's default when unset.
     */
    var workingDir: String? = null

    /**
     * Exposes a network port of this container.
     *
     * Example:
     * ```kotlin
     * addPort(8080) {
     *     name = "http"
     *     protocol = Protocol.TCP
     * }
     * ```
     *
     * @param containerPort The port number the container listens on.
     * @param prepare       Configures the [ContainerPortSpecBuilder].
     */
    fun addPort(containerPort: Int, prepare: ContainerPortSpecBuilder.() -> Unit = {}) {
        if (ports == null) {
            ports = mutableListOf()
        }
        ports!!.add(ContainerPortSpecBuilder(containerPort).apply(prepare))
    }

    /**
     * Exposes several network ports of this container.
     *
     * Example:
     * ```kotlin
     * ports {
     *     port(8080) { name = "http" }
     *     port(8443) { name = "https" }
     * }
     * ```
     *
     * @param prepare Configures the [PortSpecListBuilder].
     */
    fun ports(prepare: PortSpecListBuilder.() -> Unit = {}) =
        PortSpecListBuilder().apply(prepare)

    /**
     * Adds a single environment variable to this container.
     *
     * The method may be called repeatedly; each call appends another variable.
     *
     * Example:
     * ```kotlin
     * env("DATABASE_URL") {
     *     value = "jdbc:postgresql://localhost:5432/mydb"
     * }
     * env("PASSWORD") {
     *     fromSecret("db-credentials", "password")
     * }
     * ```
     *
     * @param name    The name of the environment variable.
     * @param prepare Configures the [SingleEnvironmentSpecBuilder].
     */
    fun env(name: String, prepare: SingleEnvironmentSpecBuilder.() -> Unit) {
        if (env == null) {
            env = mutableListOf()
        }
        env!!.add(SingleEnvironmentSpecBuilder(name).apply(prepare))
    }

    /**
     * Adds several environment variables to this container in one block.
     *
     * Example:
     * ```kotlin
     * envs {
     *     variable("LOG_LEVEL") { value = "debug" }
     *     variable("POD_NAME") { fromField("metadata.name") }
     * }
     * ```
     *
     * @param prepare Configures the [EnvironmentListBuilder].
     */
    fun envs(prepare: EnvironmentListBuilder.() -> Unit) =
        EnvironmentListBuilder().apply(prepare)

    /**
     * Imports all entries of a ConfigMap or Secret as environment variables.
     *
     * The method may be called repeatedly; each call appends another source.
     *
     * Example:
     * ```kotlin
     * envFrom {
     *     prefix = "APP_"
     *     configMapRef("app-config") {
     *         optional = true
     *     }
     * }
     * ```
     *
     * @param prepare Configures the [CompleteEnvironmentSpecBuilder].
     */
    fun envFrom(prepare: CompleteEnvironmentSpecBuilder.() -> Unit) {
        if (envFrom == null) {
            envFrom = mutableListOf()
        }
        envFrom!!.add(CompleteEnvironmentSpecBuilder().apply(prepare))
    }

    /**
     * Imports several ConfigMaps or Secrets as environment variables in one block.
     *
     * Example:
     * ```kotlin
     * envsFrom {
     *     source { configMapRef("app-config") }
     *     source { secretRef("app-secrets") }
     * }
     * ```
     *
     * @param prepare Configures the [EnvironmentFromListBuilder].
     */
    fun envsFrom(prepare: EnvironmentFromListBuilder.() -> Unit) =
        EnvironmentFromListBuilder().apply(prepare)

    /**
     * Declares the CPU, memory and extended resource requests and limits of this container.
     *
     * Example:
     * ```kotlin
     * resources {
     *     requests {
     *         cpu = 100.milliCores
     *         memory = 128.mebiBytes
     *     }
     * }
     * ```
     *
     * @param prepare Configures the [HardwareResourceSpecBuilder].
     */
    fun resources(prepare: HardwareResourceSpecBuilder.() -> Unit) {
        resources = HardwareResourceSpecBuilder().apply(prepare)
    }

    /**
     * Declares whether an in-place resize of the given resource requires a container restart.
     *
     * Example:
     * ```kotlin
     * addResizePolicy(ResourceName.Memory) {
     *     restartPolicy = ResourceResizePolicySpec.RestartPolicy.RestartContainer
     * }
     * ```
     *
     * @param resourceName The resource this policy applies to.
     * @param prepare      Configures the [ResourceResizePolicySpecBuilder].
     */
    fun addResizePolicy(resourceName: ResourceName, prepare: ResourceResizePolicySpecBuilder.() -> Unit = {}) {
        if (resizePolicy == null) {
            resizePolicy = mutableListOf()
        }
        resizePolicy!!.add(ResourceResizePolicySpecBuilder(resourceName).apply(prepare))
    }

    /**
     * Declares whether an in-place resize of the given resource requires a container restart.
     *
     * Example:
     * ```kotlin
     * addResizePolicy(ResourceName.Memory, ResourceResizePolicySpec.RestartPolicy.RestartContainer)
     * ```
     *
     * @param resourceName  The resource this policy applies to.
     * @param restartPolicy What has to happen for a change of the resource to take effect.
     */
    fun addResizePolicy(resourceName: ResourceName, restartPolicy: ResourceResizePolicySpec.RestartPolicy) =
        addResizePolicy(resourceName) { this.restartPolicy = restartPolicy }

    /**
     * Declares the in-place resize behaviour of several resources in one block.
     *
     * Example:
     * ```kotlin
     * resizePolicies {
     *     resizePolicy(ResourceName.Cpu)
     *     resizePolicy(ResourceName.Memory) {
     *         restartPolicy = ResourceResizePolicySpec.RestartPolicy.RestartContainer
     *     }
     * }
     * ```
     *
     * @param prepare Configures the [ResizePolicyListBuilder].
     */
    fun resizePolicies(prepare: ResizePolicyListBuilder.() -> Unit) =
        ResizePolicyListBuilder().apply(prepare)

    /**
     * Mounts a Pod volume into this container's filesystem.
     *
     * Example:
     * ```kotlin
     * addVolumeMount("config", "/etc/app") {
     *     readOnly = true
     *     subPath = "application.yaml"
     * }
     * ```
     *
     * @param name      The name of the volume to mount.
     * @param mountPath The absolute path inside the container the volume is mounted at.
     * @param prepare   Configures the [VolumeMountSpecBuilder].
     */
    fun addVolumeMount(name: String, mountPath: String, prepare: VolumeMountSpecBuilder.() -> Unit = {}) {
        if (volumeMounts == null) {
            volumeMounts = mutableListOf()
        }
        volumeMounts!!.add(VolumeMountSpecBuilder(name, mountPath).apply(prepare))
    }

    /**
     * Mounts several Pod volumes into this container's filesystem in one block.
     *
     * @param prepare Configures the [VolumeMountSpecListBuilder].
     */
    fun volumeMounts(prepare: VolumeMountSpecListBuilder.() -> Unit) =
        VolumeMountSpecListBuilder().apply(prepare)

    /**
     * Exposes a Pod volume to this container as a raw block device.
     *
     * @param name       The name of the volume to expose.
     * @param devicePath The absolute path inside the container the device is made available at.
     * @param prepare    Configures the [VolumeDeviceSpecBuilder].
     */
    fun addVolumeDevice(name: String, devicePath: String, prepare: VolumeDeviceSpecBuilder.() -> Unit = {}) {
        if (volumeDevices == null) {
            volumeDevices = mutableListOf()
        }
        volumeDevices!!.add(VolumeDeviceSpecBuilder(name, devicePath).apply(prepare))
    }

    /**
     * Exposes several Pod volumes to this container as raw block devices in one block.
     *
     * @param prepare Configures the [VolumeDeviceSpecListBuilder].
     */
    fun volumeDevices(prepare: VolumeDeviceSpecListBuilder.() -> Unit) =
        VolumeDeviceSpecListBuilder().apply(prepare)

    /**
     * Configures the probe deciding whether the container is still healthy.
     *
     * A failing liveness probe causes the container to be restarted.
     *
     * @param prepare Configures the [ProbeSpecBuilder].
     */
    fun livenessProbe(prepare: ProbeSpecBuilder.() -> Unit) {
        livenessProbe = ProbeSpecBuilder().apply(prepare)
    }

    /**
     * Configures the probe deciding whether the container can serve traffic.
     *
     * A failing readiness probe removes the Pod from the endpoints of its Services.
     *
     * @param prepare Configures the [ProbeSpecBuilder].
     */
    fun readinessProbe(prepare: ProbeSpecBuilder.() -> Unit) {
        readinessProbe = ProbeSpecBuilder().apply(prepare)
    }

    /**
     * Configures the probe deciding whether the application has finished starting.
     *
     * Liveness and readiness probes are suppressed until the startup probe succeeds.
     *
     * @param prepare Configures the [ProbeSpecBuilder].
     */
    fun startupProbe(prepare: ProbeSpecBuilder.() -> Unit) {
        startupProbe = ProbeSpecBuilder().apply(prepare)
    }

    /**
     * Configures the hooks executed after the container starts and before it is stopped.
     *
     * @param prepare Configures the [LifecycleSpecBuilder].
     */
    fun lifecycle(prepare: LifecycleSpecBuilder.() -> Unit) {
        lifecycle = LifecycleSpecBuilder().apply(prepare)
    }

    /**
     * Configures the security settings applied to this container.
     *
     * @param prepare Configures the [ContainerSecurityContextSpecBuilder].
     */
    fun securityContext(prepare: ContainerSecurityContextSpecBuilder.() -> Unit) {
        securityContext = ContainerSecurityContextSpecBuilder().apply(prepare)
    }

    /**
     * Overrides the image's entrypoint.
     *
     * @param command The entrypoint and its arguments.
     */
    fun command(vararg command: String) {
        if (this.command == null) {
            this.command = mutableListOf()
        }
        this.command!!.addAll(command)
    }

    /**
     * Overrides the arguments passed to the entrypoint.
     *
     * @param args The arguments to pass.
     */
    fun args(vararg args: String) {
        if (this.args == null) {
            this.args = mutableListOf()
        }
        this.args!!.addAll(args)
    }

    /**
     * Builds the configured container.
     *
     * @return A [ContainerSpec] carrying the configured values.
     */
    internal fun build() = ContainerSpec(
        name = name,
        image = image,
        imagePullPolicy = imagePullPolicy,
        ports = ports?.map { it.build() },
        env = env?.map { it.build() },
        envFrom = envFrom?.map { it.build() },
        resources = resources?.build(),
        resizePolicy = resizePolicy?.map { it.build() },
        restartPolicy = restartPolicy,
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
     * Collects several container ports.
     */
    inner class PortSpecListBuilder internal constructor() {
        /**
         * Exposes a network port of this container.
         *
         * @param containerPort The port number the container listens on.
         * @param prepare       Configures the [ContainerPortSpecBuilder].
         */
        fun port(containerPort: Int, prepare: ContainerPortSpecBuilder.() -> Unit = {}) =
            addPort(containerPort, prepare)
    }

    /**
     * Collects several in-place resize policies.
     */
    inner class ResizePolicyListBuilder internal constructor() {
        /**
         * Declares whether an in-place resize of the given resource requires a container restart.
         *
         * @param resourceName The resource this policy applies to.
         * @param prepare      Configures the [ResourceResizePolicySpecBuilder].
         */
        fun resizePolicy(resourceName: ResourceName, prepare: ResourceResizePolicySpecBuilder.() -> Unit = {}) =
            addResizePolicy(resourceName, prepare)
    }

    /**
     * Collects several environment variables.
     */
    inner class EnvironmentListBuilder internal constructor() {
        /**
         * Adds a single environment variable to this container.
         *
         * @param name    The name of the environment variable.
         * @param prepare Configures the [SingleEnvironmentSpecBuilder].
         */
        fun variable(name: String, prepare: SingleEnvironmentSpecBuilder.() -> Unit) =
            env(name, prepare)
    }

    /**
     * Collects several ConfigMap or Secret sources imported as environment variables.
     */
    inner class EnvironmentFromListBuilder internal constructor() {
        /**
         * Imports all entries of a ConfigMap or Secret as environment variables.
         *
         * @param prepare Configures the [CompleteEnvironmentSpecBuilder].
         */
        fun source(prepare: CompleteEnvironmentSpecBuilder.() -> Unit) =
            envFrom(prepare)
    }

    /**
     * Collects several volume mounts.
     */
    inner class VolumeMountSpecListBuilder internal constructor() {
        /**
         * Mounts a Pod volume into this container's filesystem.
         *
         * @param name      The name of the volume to mount.
         * @param mountPath The absolute path inside the container the volume is mounted at.
         * @param prepare   Configures the [VolumeMountSpecBuilder].
         */
        fun volumeMount(name: String, mountPath: String, prepare: VolumeMountSpecBuilder.() -> Unit = {}) =
            addVolumeMount(name, mountPath, prepare)
    }

    /**
     * Collects several raw block devices.
     */
    inner class VolumeDeviceSpecListBuilder internal constructor() {
        /**
         * Exposes a Pod volume to this container as a raw block device.
         *
         * @param name       The name of the volume to expose.
         * @param devicePath The absolute path inside the container the device is made available at.
         * @param prepare    Configures the [VolumeDeviceSpecBuilder].
         */
        fun volumeDevice(name: String, devicePath: String, prepare: VolumeDeviceSpecBuilder.() -> Unit = {}) =
            addVolumeDevice(name, devicePath, prepare)
    }
}
