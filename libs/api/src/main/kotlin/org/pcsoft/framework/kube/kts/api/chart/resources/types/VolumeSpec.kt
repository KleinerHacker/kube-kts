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

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.VolumeSpecSerializer
import org.pcsoft.framework.kube.kts.api.types.MemoryValue
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Represents a volume specification for a Kubernetes pod.
 *
 * This class defines how a volume is configured, including its name,
 * the source of the volume, and any optional empty directory configuration.
 *
 * @property name The name of the volume.
 * @property source The source of the volume, represented by a sealed interface that supports multiple specific types.
 */
@NoArgs
@JsonSerialize(using = VolumeSpecSerializer::class)
data class VolumeSpec(
    val name: String,
    val source: SourceSpec,
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
     * Represents a specification for a file source within a projected volume.
     *
     * This sealed class defines the common fields and structure for file source configurations,
     * which are used in the context of volume projections in Kubernetes. It can be extended
     * to provide specific file mapping rules or properties.
     *
     * @property name The name identifier for the file source. This may correspond to a specific volume or file source name.
     * @property optional Indicates whether the file source is optional. If true, the absence of the specified source
     *                    will not be considered an error.
     * @property defaultMode Represents the default permission mode (Unix-style) applied to all files created within this
     *                       file source. If null, a default mode specific to the container runtime may be used.
     * @property items A list of additional items detailing specific mappings or configurations related to the
     *                 file source. These items depend on the specific context or type of implementation.
     */
    @NoArgs
    sealed class FileSourceSpec(
        val name: String?,
        val optional: Boolean?,
        val defaultMode: Int?,
        val items: List<Any>?
    ) : SourceSpec {
        /**
         * Represents a mapping between a specific key in a ConfigMap and its corresponding file path
         * within a projected volume. This specification is used to customize how individual keys
         * from a Kubernetes ConfigMap are mapped to files inside the volume.
         *
         * @property key The key in the ConfigMap to be projected into the volume. This is the name of the
         *               data entry within the ConfigMap that should be used.
         * @property path The relative file path within the volume where the content of the specified key
         *                will be written. The path is relative to the mount point of the volume.
         * @property mode The file permission mode (Unix-style) to be applied to the file created for the
         *                specified key. When null, a default permission may be applied.
         */
        @NoArgs
        data class KeyToPathSpec(val key: String, val path: String, val mode: Int?)
    }

    /**
     * Defines the specification for a ConfigMap source used in a volume configuration.
     *
     * This class represents a Kubernetes ConfigMap volume source, allowing a ConfigMap to be
     * projected into a volume. It provides options to configure the name of the ConfigMap,
     * optionality, default file permissions, and key-to-path mappings.
     *
     * @property name The name of the ConfigMap to be used. When null, it indicates that a ConfigMap
     *                reference is not specified.
     * @property optional Whether the ConfigMap is optional. If true, the volume is mounted even if
     *                    the ConfigMap does not exist. If false or null, the volume mount fails when
     *                    the ConfigMap is missing.
     * @property defaultMode Default permissions mode (file mode) to be applied to the files created
     *                       from this ConfigMap. It is represented as an integer using Unix permission
     *                       notation.
     * @property items A list of key-to-path mappings, allowing specific ConfigMap keys to be projected
     *                 to custom file paths within the volume. If null, all keys in the ConfigMap are
     *                 protected into the volume with their default mappings.
     */
    @NoArgs
    class ConfigMapSourceSpec(
        name: String?,
        optional: Boolean?,
        defaultMode: Int?,
        items: List<KeyToPathSpec>?
    ) : FileSourceSpec(name, optional, defaultMode, items)

    /**
     * Represents a source specification for a secret volume in a Kubernetes-like environment.
     *
     * This class is used to define how secrets are projected into a volume. It extends the
     * functionality of [FileSourceSpec], inheriting properties that specify the source name,
     * optionality, file permission defaults, and mappings of keys to paths.
     *
     * @constructor
     * Creates an instance of `SecretSourceSpec`.
     *
     * @param name The name of the secret to be projected into the volume.
     *             If null, the name will be resolved dynamically based on the context.
     * @param optional Specifies whether the secret is optional. If true, the volume
     *                 will be created even if the secret is missing.
     * @param defaultMode The default file permission mode (Unix-style)
     *                    for files created within the volume.
     * @param items A list of key-to-path mappings, where each mapping specifies how an individual
     *              key in the secret is projected into a specific path inside the volume.
     */
    @NoArgs
    class SecretSourceSpec(
        @JsonProperty("secretName")
        name: String?,
        optional: Boolean?,
        defaultMode: Int?,
        items: List<KeyToPathSpec>?
    ) : FileSourceSpec(name, optional, defaultMode, items)

    /**
     * Represents the source of a volume backed by a PersistentVolumeClaim (PVC).
     *
     * This class is used to specify the details for a Kubernetes PersistentVolumeClaim
     * as the source for a volume in a pod. The claim must exist and be bound
     * to a PersistentVolume before it can be used by the pod.
     *
     * @property claimName The name of the PersistentVolumeClaim to be used as the source.
     * @property readOnly Whether the volume should be mounted read-only. If null, defaults to false.
     */
    @NoArgs
    data class PersistentVolumeClaimSourceSpec(val claimName: String, val readOnly: Boolean?) : SourceSpec

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
     *                the desired behavior and constraints. If null, the type will be inferred based
     *                on the path.
     */
    @NoArgs
    data class HostPathSourceSpec(val path: String, val type: Type?) : SourceSpec {
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

    /**
     * Specifies the configuration for an EmptyDir volume source.
     *
     * An EmptyDir volume is used to provide a temporary directory that is created when a pod is assigned
     * to a node and persists as long as the pod is running on that node. The contents are deleted when the
     * pod is removed from the node. The properties of this class allow customization of the storage medium and
     * size limit for the EmptyDir volume.
     *
     * @constructor Creates an instance of `EmptyDirSourceSpec`.
     * @property medium Specifies the type of storage medium to use for the EmptyDir volume. If null, the default
     * medium type is used, which generally corresponds to disk storage.
     * @property sizeLimit Defines the maximum amount of storage that can be allocated to this volume. If null,
     * there is no size limit imposed.
     */
    @NoArgs
    class EmptyDirSourceSpec(
        val medium: MediumType?,
        val sizeLimit: MemoryValue?
    ) : SourceSpec {
        /**
         * Represents the storage medium type for an EmptyDir volume.
         *
         * Defines the type of storage used by an EmptyDir volume in a container's filesystem.
         *
         * @property value The string representation of the medium type.
         */
        @Suppress("unused")
        enum class MediumType @JsonCreator constructor(@get:JsonValue val value: String) {
            /**
             * Represents a medium type that stores data in memory.
             */
            Memory("Memory"),

            /**
             * Represents a medium type that stores data on disk.
             */
            Disk("")
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