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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeSpec.SourceSpec

/**
 * A builder class for constructing volume specifications to be used in a Kubernetes pod.
 *
 * This builder provides methods for configuring various types of volume sources, such as ConfigMaps,
 * Secrets, and HostPaths. The constructed volume specification can be integrated into Kubernetes pod
 * configurations.
 *
 * Some values are required.
 *
 * @constructor Creates a builder for a volume specification with the specified name.
 * @param name The name of the volume being constructed.
 */
class VolumeSpecBuilder internal constructor(private val name: String) {
    private var source: SourceSpec? = null

    /**
     * Represents an empty directory volume that can be associated with a Kubernetes pod.
     *
     * This property is used to configure an ephemeral emptyDir volume as part of the volume
     * specification for a pod. An emptyDir volume is created when a pod is assigned to a node
     * and exists as long as the pod is running on that node. It is primarily used for temporary
     * storage purposes, such as caching or scratch data, and is deleted when the pod is terminated.
     *
     * This property accepts configuration details required for specifying the emptyDir volume.
     */
    var emptyDir: Any? = null

    /**
     * Configures the volume specification to use a ConfigMap as its source.
     *
     * A ConfigMap is a key-value store used to store configuration data that can be made available
     * to a Kubernetes pod. This method sets the source of the volume to a ConfigMap, enabling the
     * pod to access the data stored in the specified ConfigMap.
     *
     * @param name The name of the ConfigMap to use as the volume source. This must match the name
     * of an existing ConfigMap in the Kubernetes cluster.
     */
    fun fromConfigMap(name: String) {
        source = VolumeSpec.ConfigMapSourceSpec(name)
    }

    /**
     * Configures the volume specification to use a Secret as its source.
     *
     * A Secret is a Kubernetes resource used to store sensitive data such as passwords,
     * tokens, and keys. This method sets the source of the volume to a Secret, allowing
     * the pod to securely access the data stored in the specified Secret.
     *
     * @param secretName The name of the Secret to use as the volume source. This must correspond
     * to the name of an existing Secret in the Kubernetes cluster.
     */
    fun fromSecret(secretName: String) {
        source = VolumeSpec.SecretSourceSpec(secretName)
    }

    /**
     * Configures the volume specification to use a host path as its source.
     *
     * A host path represents a file or directory on the host node's filesystem. This method sets the
     * source of the volume to a specified path on the host, allowing the application running within
     * the Kubernetes pod to access the filesystem resources of the host node.
     *
     * @param path The path on the host node's filesystem to use as the volume source.
     * @param type The type of the host path, which defines how Kubernetes should interpret and validate
     * the given path on the host node (e.g., a directory, a file, etc.).
     */
    fun fromHostPath(path: String, type: VolumeSpec.HostPathSourceSpec.Type) {
        source = VolumeSpec.HostPathSourceSpec(path, type)
    }

    /**
     * Configures the source of the volume using the provided builder configuration.
     *
     * This method initializes a `FromBuilder` instance, allowing the caller to configure
     * the volume's source using methods such as `configMap`, `secret`, or `hostPath`.
     *
     * @param prepare A lambda function used to configure the `FromBuilder`. Within the lambda,
     * methods can be called to set up the desired source for the volume.
     */
    fun from(prepare: FromBuilder.() -> Unit) {
        FromBuilder().apply(prepare)
    }

    /**
     * Builds a `VolumeSpec` object using the current configuration of the `VolumeSpecBuilder`.
     *
     * The method ensures that the `source` field is set before creating the `VolumeSpec` instance.
     * If the `source` field is null, an `IllegalArgumentException` is thrown.
     *
     * @return A `VolumeSpec` instance containing the configured name, source, and emptyDir properties.
     * @throws IllegalArgumentException if the source field is not set.
     */
    internal fun build(): VolumeSpec {
        require(source != null) { "Source must be set" }

        return VolumeSpec(name, source!!, emptyDir)
    }

    /**
     * Provides a set of methods for defining the source of a volume within the `VolumeSpecBuilder`.
     *
     * The `FromBuilder` class allows convenient configuration of a volume's source, supporting
     * ConfigMaps, Secrets, and HostPaths as possible sources. Each method adjusts the volume
     * specification within the containing builder.
     *
     * This class is designed to be initialized internally and is not constructed directly by users.
     */
    inner class FromBuilder internal constructor() {
        /**
         * Configures the volume to use a specific ConfigMap as its source.
         *
         * @param name The name of the ConfigMap to be used as the volume source.
         *             This should correspond to the name of an existing ConfigMap in
         *             the Kubernetes cluster.
         */
        fun configMap(name: String) = fromConfigMap(name)

        /**
         * Configures the volume to use a specific Secret as its source.
         *
         * A Secret is a Kubernetes resource used to store sensitive data such as passwords,
         * tokens, and keys. This method sets the source of the volume to a Secret, allowing
         * the pod to securely access the data stored in the specified Secret.
         *
         * @param secretName The name of the Secret to use as the volume source.
         *                   This must correspond to the name of an existing Secret in the Kubernetes cluster.
         */
        fun secret(secretName: String) = fromSecret(secretName)

        /**
         * Configures the volume to use a specific host path as its source.
         *
         * A host path is a file or directory on the node file system. By using this method,
         * the volume is set up to reference the specified path on the host where the pod is running.
         *
         * @param path The absolute path on the host machine to be used as the volume source.
         *             This path must exist on the node.
         * @param type The type of the host path, which defines the expected behavior of the path
         *             (e.g., whether it should exist, be a directory, etc.).
         */
        fun hostPath(path: String, type: VolumeSpec.HostPathSourceSpec.Type) = fromHostPath(path, type)
    }
}

/**
 * Builder class for constructing instances of `VolumeMountSpec`.
 *
 * This class provides a fluent interface to configure the properties of a volume mount,
 * including the volume name, its mount path inside the container, and optional read-only mode.
 *
 * @constructor Initializes the builder with the required `name` and `mountPath` values.
 *
 * @param name The name of the volume to be mounted.
 * @param mountPath The file system path inside the container where the volume will be mounted.
 *
 * @property readOnly Specifies whether the volume should be mounted as read-only.
 *                     If set to `null`, the default behavior is determined by the container runtime or orchestrator.
 */
class VolumeMountSpecBuilder internal constructor(private val name: String, private val mountPath: String) {
    /**
     * Indicates whether the associated volume should be mounted as read-only.
     *
     * If set to `true`, the volume is mounted in a mode that prevents write operations.
     * If set to `false`, the volume is mounted with read-write access.
     * A value of `null` allows the behavior to be determined by the container runtime
     * or orchestrator's default configuration.
     */
    var readOnly: Boolean? = null

    /**
     * Constructs and returns an instance of `VolumeMountSpec` using the configured properties.
     *
     * This method finalizes the build process for a `VolumeMountSpec`, applying the values
     * for the volume name, mount path, and read-only flag as set on the builder instance.
     *
     * @return An instance of `VolumeMountSpec` initialized with the builder's properties.
     */
    internal fun build() = VolumeMountSpec(name, mountPath, readOnly)
}

/**
 * Builder class for constructing instances of `VolumeDeviceSpec`.
 *
 * This builder is used to create and configure a `VolumeDeviceSpec` object, which defines
 * the specification of a volume device in Kubernetes. The specification includes the name
 * of the device and the path where it will be accessible inside the container.
 *
 * @constructor Creates a `VolumeDeviceSpecBuilder` with the given name and device path.
 * @param name The name of the volume device.
 * @param devicePath The path inside the container where the volume device will be mounted.
 */
class VolumeDeviceSpecBuilder internal constructor(private val name: String, private val devicePath: String) {
    /**
     * Builds a `VolumeDeviceSpec` instance using the configured name and device path.
     *
     * This method constructs and returns a new instance of the `VolumeDeviceSpec` class
     * based on the values provided to the `VolumeDeviceSpecBuilder`. The resulting object
     * represents the specification for a volume device, which includes its name and
     * the path where the device will be mounted inside the container.
     *
     * @return A new instance of `VolumeDeviceSpec` with the configured properties.
     */
    internal fun build() = VolumeDeviceSpec(name, devicePath)
}