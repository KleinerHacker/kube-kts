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
 * Represents a volume specification for a Kubernetes pod.
 *
 * This class defines how a volume is configured, including its name,
 * the source of the volume, and any optional empty directory configuration.
 *
 * @property name The name of the volume.
 * @property source The source of the volume, represented by a sealed interface that supports multiple specific types.
 * @property emptyDir An optional configuration for an empty directory volume. If non-null, this volume will use an empty directory for storage.
 */
@NoArgs
data class VolumeSpec(
    val name: String,
    val source: SourceSpec,
    val emptyDir: Any?
) {
    /**
     * Represents the source specification for a volume in a Kubernetes pod.
     *
     * This interface serves as the base type for defining various volume source configurations,
     * such as ConfigMap, Secret, PersistentVolumeClaim, HostPath, and others. Implementations of this interface
     * provide the details specific to the chosen volume source type.
     */
    sealed interface SourceSpec

    /**
     * Represents a volume source configuration backed by a Kubernetes ConfigMap.
     *
     * This class is used to define a ConfigMap as the source for a volume in a Kubernetes pod,
     * allowing the pod to access the configuration data stored in the ConfigMap.
     *
     * @property name The name of the ConfigMap to be used as the volume source.
     */
    @NoArgs
    data class ConfigMapSourceSpec(val name: String) : SourceSpec

    /**
     * Represents a volume source specification that uses a Kubernetes Secret as its source.
     *
     * This class is a specific implementation of the `SourceSpec` interface and allows
     * a Kubernetes Secret to be mounted as a volume in a pod. The secret is identified
     * by its name, which corresponds to an existing Kubernetes Secret resource in the
     * same namespace as the pod.
     *
     * @property secretName The name of the Kubernetes Secret to be used as the volume source.
     */
    @NoArgs
    data class SecretSourceSpec(val secretName: String) : SourceSpec

    /**
     * Represents the source of a volume backed by a PersistentVolumeClaim (PVC).
     *
     * This class is used to specify the details for a Kubernetes PersistentVolumeClaim
     * as the source for a volume in a pod. The claim must exist and be bound
     * to a PersistentVolume before it can be used by the pod.
     *
     * @property claimName The name of the PersistentVolumeClaim to be used as the source.
     */
    @NoArgs
    data class PersistentVolumeClaimSourceSpec(val claimName: String) : SourceSpec

    /**
     * Specifies the configuration for a HostPath volume source.
     *
     * HostPath volumes allow the use of files or directories on the host node's filesystem.
     * This class defines the path and type of the file system object that will be used as
     * the volume in a Kubernetes pod.
     *
     * @property path The absolute path on the host machine that the volume should reference.
     *                This path must exist on the host for certain types, or it may be created
     *                depending on the specified type.
     * @property type The type of the HostPath volume, which determines the required behavior or
     *                object type (e.g., file, directory, socket). Use [Type] enumeration to indicate
     *                the desired behavior and constraints.
     */
    @NoArgs
    data class HostPathSourceSpec(val path: String, val type: Type) : SourceSpec {
        /**
         * Defines the possible types of file system objects for a HostPath volume.
         *
         * The type indicates the specific nature or behavior of the file system object
         * located at the specified path on the host machine. It is used to enforce
         * constraints or take specific actions, such as creating a file or directory
         * if it does not exist.
         */
        @Suppress("unused")
        enum class Type {
            /**
             * Represents the absence of any specific file system object type in the context of a HostPath volume.
             *
             * This type is used to indicate that no constraints or specific actions should be applied
             * to the file system object located at the specified path on the host machine.
             */
            None {
                override fun toString(): String = ""
            },

            /**
             * Represents a directory on the host path that is created if it does not exist.
             *
             * This type is used in the context of a HostPath volume to ensure that a directory
             * is available at the specified path on the host machine. If the directory does not
             * already exist, it will be automatically created.
             */
            DirectoryOrCreate,

            /**
             * Represents a directory on the host path.
             *
             * This type is used in scenarios where a specific directory already exists and no additional
             * creation or enforcement logic is required. It ensures that the specified path is treated
             * strictly as an existing directory during operations involving HostPath volumes.
             */
            Directory,

            /**
             * Represents a file on the host path that will be created if it does not exist.
             *
             * This type is used in HostPath volumes to ensure that a regular file is present at
             * the specified path on the host machine. If the file does not already exist, it will be
             * automatically created as part of the operation.
             */
            FileOrCreate,

            /**
             * Represents a file within a given structure or context.
             *
             * This class is intended to encapsulate file-related information or functionality
             * that can be utilized as part of broader resources or configurations.
             */
            File,

            /**
             * Represents a network socket configuration for a resource.
             *
             * This class is used to define the properties of a socket, including the target port,
             * the transport layer protocol, and optional settings tailored to specific connection requirements.
             *
             * Sockets are integral components of networking in applications, enabling communication
             * between processes over a network by binding a protocol and port together.
             */
            Socket,

            /**
             * Represents a character device resource within the system.
             *
             * A character device is a type of hardware device that transmits data
             * one character at a time. This class is used to configure and represent
             * such devices when interacting with system resources or configurations.
             */
            CharDevice,

            /**
             * Represents a block device used in container or pod configurations.
             *
             * This class is typically used to define and manage persistent storage resources,
             * such as volumes or disk devices, that a container or pod can consume.
             * It provides functionality to attach and manage such devices in a Kubernetes environment.
             */
            BlockDevice
        }
    }
}

/**
 * Describes the specification for mounting a volume onto a container.
 *
 * This class is used to configure how a volume should be attached to a specific container,
 * including its mount path and optional read-only access mode. It is typically used in
 * container orchestration scenarios to provide persistent or ephemeral storage to containers.
 *
 * @property name The name of the volume to be mounted.
 * @property mountPath The file system path inside the container where the volume will be mounted.
 * @property readOnly Indicates whether the volume should be mounted as read-only. If null,
 *                    the default behavior is determined by the container runtime or orchestrator.
 */
@NoArgs
data class VolumeMountSpec(
    val name: String,
    val mountPath: String,
    val readOnly: Boolean?
)

/**
 * Represents the specification for a volume device.
 *
 * This class is used to define a volume device configuration, including the device's name
 * and the path where the device is mounted inside the container. It is typically used
 * in scenarios involving persistent storage or attaching block devices to containers.
 *
 * @property name The name of the volume device.
 * @property devicePath The path inside the container where the device is exposed.
 */
@NoArgs
data class VolumeDeviceSpec(
    val name: String,
    val devicePath: String
)