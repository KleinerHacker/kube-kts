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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeSpec.EmptyDirSourceSpec.MediumType
import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeSpec.HostPathSourceSpec.Type
import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeSpec.SourceSpec
import org.pcsoft.framework.kube.kts.api.types.MemoryValue

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
    private var source: SourceSpecBuilder<*>? = null

    /**
     * Configures the volume specification to use a ConfigMap as its source.
     *
     * A ConfigMap is a key-value store used to store configuration data that can be made available
     * to a Kubernetes pod. This method sets the source of the volume to a ConfigMap, enabling the
     * pod to access the data stored in the specified ConfigMap.
     *
     * Example:
     * ```kotlin
     * fromConfigMap {
     *     name = "app-config"
     *     optional = true
     *     defaultMode = 0644
     * }
     * ```
     *
     * @param preparer A lambda function that configures the `ConfigMapSourceSpecBuilder` for the volume source.
     */
    fun fromConfigMap(preparer: ConfigMapSourceSpecBuilder.() -> Unit) {
        source = ConfigMapSourceSpecBuilder().apply(preparer)
    }

    /**
     * Configures the volume specification to use a Secret as its source.
     *
     * A Secret is a Kubernetes resource used to store sensitive data such as passwords,
     * tokens, and keys. This method sets the source of the volume to a Secret, allowing
     * the pod to securely access the data stored in the specified Secret.
     *
     * Example:
     * ```kotlin
     * fromSecret {
     *     name = "db-credentials"
     *     optional = false
     *     defaultMode = 0400
     * }
     * ```
     *
     * @param preparer A lambda function that configures the `SecretSourceSpecBuilder` for the volume source.
     */
    fun fromSecret(preparer: SecretSourceSpecBuilder.() -> Unit) {
        source = SecretSourceSpecBuilder().apply(preparer)
    }

    /**
     * Configures the volume specification to use a PersistentVolumeClaim as its source.
     *
     * Example:
     * ```kotlin
     * fromPersistentVolumeClaim("my-pvc") {
     *     readOnly = true
     * }
     * ```
     *
     * @param claimName The name of the PersistentVolumeClaim to use as the volume source.
     * @param prepare A lambda function that configures the `PersistentVolumeClaimSourceSpecBuilder` for the volume source.
     */
    fun fromPersistentVolumeClaim(claimName: String, prepare: PersistentVolumeClaimSourceSpecBuilder.() -> Unit = {}) {
        source = PersistentVolumeClaimSourceSpecBuilder(claimName).apply(prepare)
    }

    /**
     * Configures the volume specification to use a host path as its source.
     *
     * A host path represents a file or directory on the host node's filesystem. This method sets the
     * source of the volume to a specified path on the host, allowing the application running within
     * the Kubernetes pod to access the filesystem resources of the host node.
     *
     * Example:
     * ```kotlin
     * fromHostPath("/var/log") {
     *     type = Type.Directory
     * }
     * ```
     *
     * @param path The path on the host node's filesystem to use as the volume source.
     * @param prepare A lambda function that configures the `HostPathSourceSpecBuilder` for the volume source.
     */
    fun fromHostPath(path: String, prepare: HostPathSourceSpecBuilder.() -> Unit = {}) {
        source = HostPathSourceSpecBuilder(path).apply(prepare)
    }

    /**
     * Configures the source of the volume using the provided builder configuration.
     *
     * This method initializes a `FromBuilder` instance, allowing the caller to configure
     * the volume's source using methods such as `configMap`, `secret`, or `hostPath`.
     *
     * Example:
     * ```kotlin
     * from {
     *     configMap {
     *         name = "app-config"
     *     }
     * }
     * ```
     *
     * @param prepare A lambda function used to configure the `FromBuilder`. Within the lambda,
     * methods can be called to set up the desired source for the volume.
     */
    fun from(prepare: FromBuilder.() -> Unit) {
        FromBuilder().apply(prepare)
    }

    /**
     * Configures the volume specification to use an EmptyDir as its source.
     *
     * Example:
     * ```kotlin
     * emptyDir {
     *     medium = MediumType.Memory
     *     sizeLimit = MemoryValue.ofGigabytes(1)
     * }
     * ```
     *
     * @param prepare A lambda function that configures the `EmptyDirSpecBuilder` for the volume source.
     */
    fun emptyDir(prepare: EmptyDirSpecBuilder.() -> Unit) {
        source = EmptyDirSpecBuilder().apply(prepare)
    }

    /**
     * Builds a `VolumeSpec` object using the current configuration of the `VolumeSpecBuilder`.
     *
     * The method ensures that the `source` field is set before creating the `VolumeSpec` instance.
     * If the `source` field is null, an `IllegalArgumentException` is thrown.
     *
     * @return A `VolumeSpec` instance containing the configured name and source properties.
     * @throws IllegalArgumentException if the source field is not set.
     */
    internal fun build(): VolumeSpec {
        require(source != null) { "Source must be set" }

        return VolumeSpec(name, source!!.build())
    }

    /**
     * Represents a builder interface for constructing instances of `SourceSpec`.
     *
     * This interface is intended to be implemented by classes responsible for configuring specific
     * volume source types for use in Kubernetes pods. Implementing classes provide methods to
     * define and customize volume source configurations, such as ConfigMaps, Secrets, PersistentVolumeClaims,
     * HostPaths, and other supported source types.
     *
     * @param T The specific type of `SourceSpec` to be built by the implementing class.
     */
    sealed interface SourceSpecBuilder<T : SourceSpec> {
        fun build(): T
    }

    /**
     * A base class for building file source specifications for a volume.
     *
     * This builder class is designed to configure volume sources that map specific keys to paths
     * within the volume. It provides functionality to specify name, optionality, and file mode
     * permissions for the file source. Key-path mapping can be configured using `addItem` or
     * the `ItemListBuilder`.
     *
     * @param T The type of `VolumeSpec.FileSourceSpec` being built.
     */
    sealed class FileSourceSpecBuilder<T : VolumeSpec.FileSourceSpec> : SourceSpecBuilder<T> {
        protected var items: MutableList<KeyToPathSpecBuilder>? = null; private set

        /**
         * The name of the file source for the volume specification.
         *
         * This property specifies the name of the file source that maps specific keys to paths
         * within the volume. It is optional and can be `null` if a name is not explicitly provided.
         */
        var name: String? = null

        /**
         * Specifies whether the file source is optional within the volume specification.
         *
         * If set to `true`, it indicates that the file source is optional and the absence of the
         * file source will not result in an error. If set to `false`, the file source is required
         * and its absence will cause an error. If `null`, this property is not explicitly set.
         */
        var optional: Boolean? = null

        /**
         * Specifies the default file mode permissions for the file source.
         *
         * This property defines the default file mode permissions that will be applied to the file
         * source if no specific mode is provided. It is optional and can be `null` if a default mode
         * is not explicitly set.
         */
        var defaultMode: Int? = null

        /**
         * Adds an item to the internal list of key-to-path specifications.
         *
         * Example:
         * ```kotlin
         * addItem("config-key", "config/app.conf") {
         *     mode = 0644
         * }
         * ```
         *
         * @param key A unique identifier representing the key to add.
         * @param path The path associated with the provided key.
         * @param prepare A lambda function used to configure the `KeyToPathSpecBuilder` with additional properties.
         */
        fun addItem(key: String, path: String, prepare: KeyToPathSpecBuilder.() -> Unit) {
            if (items == null) {
                items = mutableListOf()
            }
            items!!.add(KeyToPathSpecBuilder(key, path).apply(prepare))
        }

        /**
         * Configures a list of items using the provided lambda function.
         *
         * Example:
         * ```kotlin
         * items {
         *     item("key1", "path/to/file1") {
         *         mode = 0644
         *     }
         *     item("key2", "path/to/file2") {}
         * }
         * ```
         *
         * @param prepare A lambda function used to configure the `ItemListBuilder` with item specifications.
         */
        fun items(prepare: ItemListBuilder.() -> Unit) =
            ItemListBuilder().apply(prepare)

        /**
         * A builder class responsible for constructing instances of `KeyToPathSpec` with specific properties.
         *
         * @constructor Creates a `KeyToPathSpecBuilder` initialized with the provided `key` and `path`.
         * @param key A unique identifier that represents the key for the specification.
         * @param path The path associated with the provided key in the specification.
         *
         * @property mode Optional mode that can be configured for the `KeyToPathSpec`.
         *
         * This class is designed for internal usage within the enclosing class `FileSourceSpecBuilder`
         * and is used to define and prepare key-to-path mappings for file source specifications.
         */
        class KeyToPathSpecBuilder internal constructor(private val key: String, private val path: String) {
            /**
             * An optional mode configuration for the key-to-path specification.
             *
             * This variable allows customization of the behavior or properties associated with the
             * key and path defined within the `KeyToPathSpecBuilder`.
             *
             * Possible values and their interpretation depend on the context in which the builder is used.
             * A `null` value indicates that no specific mode has been set.
             */
            var mode: Int? = null

            /**
             * Builds and returns an instance of `KeyToPathSpec` using the configured key, path, and mode.
             *
             * @return A new instance of `VolumeSpec.FileSourceSpec.KeyToPathSpec` constructed with the specified key, path, and mode.
             */
            internal fun build() = VolumeSpec.FileSourceSpec.KeyToPathSpec(key, path, mode)
        }

        /**
         * A builder class responsible for configuring a list of items within a file source specification.
         */
        inner class ItemListBuilder internal constructor() {
            /**
             * Configures and adds an item defined by a key-path mapping to the internal list of specifications.
             *
             * Example:
             * ```kotlin
             * item("database-url", "config/db.conf") {
             *     mode = 0600
             * }
             * ```
             *
             * @param key A unique identifier that represents the key to be added.
             * @param path The path associated with the provided key.
             * @param prepare A lambda function used to configure the `KeyToPathSpecBuilder` with additional properties.
             */
            fun item(key: String, path: String, prepare: KeyToPathSpecBuilder.() -> Unit = {}) =
                addItem(key, path, prepare)
        }
    }

    /**
     * A builder class used to configure and construct the specification for an `EmptyDir` volume.
     *
     * The `EmptyDir` volume is an ephemeral storage solution in Kubernetes, where data is preserved
     * for the lifetime of the pod. The builder allows configuration of properties such as the storage
     * medium and the size limit of the volume.
     */
    class EmptyDirSpecBuilder internal constructor() : SourceSpecBuilder<VolumeSpec.EmptyDirSourceSpec> {
        /**
         * Specifies the storage medium type for the `EmptyDir` volume.
         * If not specified, the default value is `Disk`.
         */
        var medium: MediumType? = null

        /**
         * Specifies the size limit for the `EmptyDir` volume.
         * If not specified, the default value is `null`, which means no size limit is enforced.
         */
        var sizeLimit: MemoryValue? = null

        /**
         * Constructs an instance of `VolumeSpec.EmptyDirSpec` based on the current configuration
         * of the `EmptyDirSpecBuilder`.
         *
         * The resulting `EmptyDirSpec` encapsulates the specified storage medium and size limit
         * for the `EmptyDir` volume.
         *
         * @return A configured `VolumeSpec.EmptyDirSpec` instance.
         * @see VolumeSpec.EmptyDirSourceSpec Represents the specification for an `EmptyDir` volume.
         */
        override fun build() = VolumeSpec.EmptyDirSourceSpec(medium, sizeLimit)
    }

    /**
     * A builder class responsible for configuring the source of a `Secret` volume within a Kubernetes pod.
     */
    class ConfigMapSourceSpecBuilder internal constructor() : FileSourceSpecBuilder<VolumeSpec.ConfigMapSourceSpec>() {
        /**
         * Constructs and returns a `VolumeSpec.ConfigMapSourceSpec` instance based on the builder's current configuration.
         *
         * This method aggregates the various properties set on the builder, such as `name`, `optional`,
         * `defaultMode`, and `items`, to produce a configured `ConfigMapSourceSpec` object.
         *
         * @return A new instance of `VolumeSpec.ConfigMapSourceSpec` containing the configuration
         *         specified in the builder.
         */
        override fun build(): VolumeSpec.ConfigMapSourceSpec =
            VolumeSpec.ConfigMapSourceSpec(name, optional, defaultMode, items?.map { it.build() })
    }

    /**
     * A builder class responsible for configuring the source of a `Secret` volume within a Kubernetes pod.
     */
    class SecretSourceSpecBuilder internal constructor() : FileSourceSpecBuilder<VolumeSpec.SecretSourceSpec>() {
        /**
         * Builds an instance of `VolumeSpec.SecretSourceSpec` using the current configuration of the builder.
         *
         * @return A configured instance of `VolumeSpec.SecretSourceSpec` with the specified name, optional flag,
         * default mode, and item mappings, if any.
         */
        override fun build(): VolumeSpec.SecretSourceSpec =
            VolumeSpec.SecretSourceSpec(name, optional, defaultMode, items?.map { it.build() })

    }

    /**
     * A builder class for constructing a `PersistentVolumeClaimSourceSpec` instance.
     *
     * This builder is used to configure a Kubernetes volume whose source is a PersistentVolumeClaim (PVC).
     * PersistentVolumeClaims are used to request storage resources in a Kubernetes cluster. By defining
     * the name of the claim and optional read-only access, this builder enables the creation of a volume
     * specification that references the desired PVC.
     *
     * @constructor This class is intended to be instantiated internally with the required PVC claim name.
     * @param claimName The name of the PersistentVolumeClaim to be used as the volume's source.
     */
    class PersistentVolumeClaimSourceSpecBuilder internal constructor(val claimName: String) : SourceSpecBuilder<VolumeSpec.PersistentVolumeClaimSourceSpec> {
        /**
         * Specifies whether the volume should be mounted as read-only.
         * If set to `true`, the volume will be mounted in a read-only mode.
         * If set to `false`, the volume will be mounted in read-write mode.
         * If not specified, the default value is `null`, which means the behavior is determined by the
         * Kubernetes cluster's configuration.
         */
        var readOnly: Boolean? = null

        /**
         * Builds and returns a `PersistentVolumeClaimSourceSpec` instance based on the provided configuration.
         *
         * This method creates a `PersistentVolumeClaimSourceSpec` object representing a PersistentVolumeClaim
         * used as a source for a Kubernetes volume. The returned object encapsulates the name of the claim and
         * whether the volume should be mounted as read-only.
         *
         * @return A `PersistentVolumeClaimSourceSpec` instance containing the configured PersistentVolumeClaim
         *         details such as the claim name and read-only access setting.
         */
        override fun build(): VolumeSpec.PersistentVolumeClaimSourceSpec =
            VolumeSpec.PersistentVolumeClaimSourceSpec(claimName, readOnly)

    }

    /**
     * Represents a specification for configuring a volume source that is backed by a host path on the
     * filesystem of the node where the pod is running.
     *
     * A host path allows the pod to access files or directories on the host node's filesystem. This
     * can be useful for scenarios where the application running within the pod needs access to shared
     * resources or specific paths available on the host.
     *
     * @constructor Creates a `HostPathSourceSpec` with the specified path.
     * @param path The path on the host node's filesystem to be used as the volume source.
     */
    class HostPathSourceSpecBuilder internal constructor(val path: String) : SourceSpecBuilder<VolumeSpec.HostPathSourceSpec> {
        /**
         * Specifies the type of the HostPath volume.
         *
         * This property defines the nature or behavior of the file system object
         * located at the specified path on the host machine. It is used to apply
         * constraints or take specific actions, like creating a file or directory
         * if it does not exist.
         *
         * The possible values for this property are defined in the `Type` enum,
         * which includes options such as `None`, `Directory`, `File`, `Socket`,
         * `CharDevice`, and `BlockDevice`.
         *
         * - `null` indicates that no specific type has been set for the HostPath volume.
         */
        var type: Type? = null

        /**
         * Builds and returns an instance of `VolumeSpec.HostPathSourceSpec` representing
         * a specification for a HostPath volume source.
         *
         * The resulting specification includes the defined path on the host node's filesystem
         * and an optional type that describes the expected nature of the host path.
         *
         * @return A `VolumeSpec.HostPathSourceSpec` instance containing the configured path and type.
         */
        override fun build(): VolumeSpec.HostPathSourceSpec =
            VolumeSpec.HostPathSourceSpec(path, type)
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
         * Example:
         * ```kotlin
         * configMap {
         *     name = "app-config"
         *     optional = true
         * }
         * ```
         *
         * @param prepare A lambda function that configures the `ConfigMapSourceSpecBuilder` for the volume source.
         */
        fun configMap(prepare: ConfigMapSourceSpecBuilder.() -> Unit) = fromConfigMap(prepare)

        /**
         * Configures the volume to use a specific Secret as its source.
         *
         * A Secret is a Kubernetes resource used to store sensitive data such as passwords,
         * tokens, and keys. This method sets the source of the volume to a Secret, allowing
         * the pod to securely access the data stored in the specified Secret.
         *
         * Example:
         * ```kotlin
         * secret {
         *     name = "api-token"
         *     defaultMode = 0400
         * }
         * ```
         *
         * @param prepare A lambda function that configures the `SecretSourceSpecBuilder` for the volume source.
         */
        fun secret(prepare: SecretSourceSpecBuilder.() -> Unit) = fromSecret(prepare)

        /**
         * Configures the volume to use a specific PersistentVolumeClaim as its source.
         *
         * Example:
         * ```kotlin
         * persistentVolumeClaim("data-pvc") {
         *     readOnly = false
         * }
         * ```
         *
         * @param claimName The name of the PersistentVolumeClaim to use as the volume source.
         * @param prepare A lambda function that configures the `PersistentVolumeClaimSourceSpecBuilder` for the volume source.
         */
        fun persistentVolumeClaim(claimName: String, prepare: PersistentVolumeClaimSourceSpecBuilder.() -> Unit = {}) =
            fromPersistentVolumeClaim(claimName, prepare)

        /**
         * Configures the volume to use a specific host path as its source.
         *
         * A host path is a file or directory on the node file system. By using this method,
         * the volume is set up to reference the specified path on the host where the pod is running.
         *
         * Example:
         * ```kotlin
         * hostPath("/mnt/data") {
         *     type = Type.DirectoryOrCreate
         * }
         * ```
         *
         * @param path The absolute path on the host machine to be used as the volume source.
         *             This path must exist on the node.
         * @param prepare A lambda function that configures the `HostPathSourceSpecBuilder` for the volume source.
         */
        fun hostPath(path: String, prepare: HostPathSourceSpecBuilder.() -> Unit = {}) = fromHostPath(path, prepare)
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
//TODO: Implement projected, downwardAPI, ephemeral, csi, nfs, awsElasticBlockStore, gcePersistentDisk, azureDisk,
// azureFile, cephfs, iscsi, local, flexVolume
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