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

/**
 * Represents the specification for a container.
 *
 * This class defines the configuration required for a container, including attributes such as
 * its name, image details, resource requirements, environment variables, probes, and other
 * runtime characteristics. It serves as a model for defining container specifications in
 * deployment configurations or similar contexts.
 *
 * @property name The name of the container.
 * @property image The image to be used for the container.
 * @property imagePullPolicy The policy determining how the container's image should be pulled.
 *                           It can be `Always`, `IfNotPresent`, or `Never`.
 * @property ports The list of ports exposed by the container.
 * @property env The specification for individual environment variables for the container.
 * @property envFrom The specification for injecting environment variables from external sources
 *                   such as ConfigMaps or Secrets.
 * @property resources The resource requirements and limits for the container.
 * @property volumeMounts The list of volumes to be mounted in the container.
 * @property volumeDevices The list of volume devices attached to the container.
 * @property livenessProbe The probe used to check the container's liveness.
 * @property readinessProbe The probe used to check the container's readiness.
 * @property startupProbe The probe used to check the container's startup process.
 * @property lifecycle The lifecycle hooks for the container, defining actions during its lifecycle.
 * @property terminationMessagePath The path where termination messages are written.
 * @property terminationMessagePolicy The policy for processing termination messages.
 *                                    It can be `File` or `FallbackToLogsOnError`.
 * @property stdin Whether the container should allocate a stdin stream.
 * @property stdinOnce Whether the container's stdin should be available only once.
 * @property tty Whether the container should allocate a TTY session.
 * @property securityContext The security context for the container, defining security-related options.
 * @property command The command to be executed when starting the container.
 * @property args The arguments to be passed to the command executed in the container.
 * @property workingDir The working directory for the container.
 */
@NoArgs
data class ContainerSpec(
    val name: String,
    val image: String,
    val imagePullPolicy: ImagePullPolicy?,
    val ports: List<PortSpec>?,
    val env: SingleEnvironmentSpec?,
    val envFrom: CompleteEnvironmentSpec?,
    val resources: HardwareResourceSpec?,
    val volumeMounts: List<VolumeMountSpec>?,
    val volumeDevices: List<VolumeDeviceSpec>?,
    val livenessProbe: ProbeSpec?,
    val readinessProbe: ProbeSpec?,
    val startupProbe: ProbeSpec?,
    val lifecycle: LifecycleSpec?,
    val terminationMessagePath: String?,
    val terminationMessagePolicy: TerminationMessagePolicy?,
    val stdin: Boolean?,
    val stdinOnce: Boolean?,
    val tty: Boolean?,
    val securityContext: SecurityContextSpec?,
    val command: List<String>?,
    val args: List<String>?,
    val workingDir: String?,
) {
    /**
     * Defines the policy for pulling container images in Kubernetes.
     *
     * The `ImagePullPolicy` determines under what conditions the container runtime
     * should pull the image for a container from the container registry.
     * This setting is crucial for managing image updates, optimizing resource usage,
     * and ensuring the desired version of the image, particularly in dynamic environments.
     */
    @Suppress("unused")
    enum class ImagePullPolicy {
        Always,
        IfNotPresent,
        Never
    }

    /**
     * Specifies the policy for capturing the termination message of a container.
     *
     * This enum defines the strategy for retrieving and handling the termination message
     * of a container upon its lifecycle events such as completion or failure.
     */
    @Suppress("unused")
    enum class TerminationMessagePolicy {
        File,
        FallbackToLogsOnError
    }

    /**
     * Defines the specification for a port in a container.
     *
     * Represents a mapping of a container port with an optional name and protocol.
     * This class is typically used to configure network communication for a container
     * by specifying the port number and protocol type.
     *
     * @property name The name of the port. This is an optional identifier used for referencing the port.
     * @property containerPort The port number to be exposed by the container. This is a mandatory field.
     * @property protocol The protocol to be used for the port. Can be TCP, UDP, or SCTP. Defaults to TCP if not specified.
     */
    @NoArgs
    data class PortSpec(
        val name: String?,
        val containerPort: Int,
        val protocol: Protocol?
    )
}