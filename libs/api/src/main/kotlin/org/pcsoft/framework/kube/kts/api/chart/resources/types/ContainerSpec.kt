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
 * Represents a single container running inside a Pod.
 *
 * A container bundles an image with the runtime configuration needed to execute it: the ports it
 * exposes, its environment, its resource requirements, the volumes it mounts, its health probes and
 * its security context.
 *
 * @property name                     The name of the container. Must be unique within the Pod.
 * @property image                     The container image to run.
 * @property imagePullPolicy           Controls when the [image] is pulled from the registry.
 * @property ports                     The network ports exposed by this container.
 * @property env                       The environment variables set for this container. Each entry either
 *                                     carries a literal value or references a field, ConfigMap key or Secret key.
 * @property envFrom                   Sources whose entries are imported wholesale as environment variables.
 *                                     Each entry references one ConfigMap or Secret.
 * @property resources                 The CPU, memory and extended resource requests and limits.
 * @property resizePolicy              Declares, per resource, whether an in-place resize requires a container restart.
 * @property restartPolicy             Overrides the Pod's restart policy for this container. Setting
 *                                     [RestartPolicy.Always] on an init container turns it into a native sidecar.
 * @property volumeMounts              The volumes mounted into this container's filesystem.
 * @property volumeDevices             The raw block devices made available to this container.
 * @property livenessProbe             Determines whether the container is still healthy. A failure restarts it.
 * @property readinessProbe            Determines whether the container can serve traffic. A failure removes it
 *                                     from Service endpoints.
 * @property startupProbe              Determines whether the application has finished starting. Liveness and
 *                                     readiness probes are suppressed until it succeeds.
 * @property lifecycle                 Hooks executed after the container starts and before it is stopped.
 * @property terminationMessagePath    The file the container writes its termination message to.
 * @property terminationMessagePolicy  Controls how the termination message is derived.
 * @property stdin                     If true, allocates a buffer for standard input.
 * @property stdinOnce                 If true, standard input is closed after the first attached session ends.
 * @property tty                       If true, allocates a pseudo-TTY for the container.
 * @property securityContext           The security settings applied to this container.
 * @property command                   Overrides the image's entrypoint.
 * @property args                      Overrides the arguments passed to the entrypoint.
 * @property workingDir                The working directory the entrypoint is executed in.
 */
@NoArgs
data class ContainerSpec(
    val name: String,
    val image: String,
    val imagePullPolicy: ImagePullPolicy?,
    val ports: List<ContainerPortSpec>?,
    val env: List<SingleEnvironmentSpec>?,
    val envFrom: List<CompleteEnvironmentSpec>?,
    val resources: HardwareResourceSpec?,
    val resizePolicy: List<ResourceResizePolicySpec>?,
    val restartPolicy: RestartPolicy?,
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
     * Controls when the container image is pulled from the registry.
     */
    @Suppress("unused")
    enum class ImagePullPolicy {
        /**
         * The image is pulled on every Pod start.
         */
        Always,

        /**
         * The image is pulled only if it is not already present on the node.
         */
        IfNotPresent,

        /**
         * The image is never pulled; it must already be present on the node.
         */
        Never
    }

    /**
     * Controls how the termination message of a container is derived.
     */
    @Suppress("unused")
    enum class TerminationMessagePolicy {
        /**
         * The message is read from the file at `terminationMessagePath`.
         */
        File,

        /**
         * The message is read from the file at `terminationMessagePath`; if that file is empty and the
         * container failed, the tail of its log is used instead.
         */
        FallbackToLogsOnError
    }

    /**
     * Overrides the Pod-level restart policy for an individual container.
     *
     * The only value Kubernetes accepts here is [Always], and only on init containers: it turns the init
     * container into a native sidecar that keeps running alongside the Pod's main containers.
     */
    @Suppress("unused")
    enum class RestartPolicy {
        /**
         * The container is kept running for the lifetime of the Pod. On an init container this declares
         * a native sidecar.
         */
        Always
    }
}
