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

@file:Suppress("DEPRECATION")

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.chart.resources.types.EmptyDirSourceSpec.MediumType
import org.pcsoft.framework.kube.kts.api.chart.resources.types.HostPathSourceSpec.Type
import org.pcsoft.framework.kube.kts.api.chart.resources.types.VolumeSpec.SourceSpec
import org.pcsoft.framework.kube.kts.api.types.MemoryValue

/**
 * Builder for a [VolumeSpec], declaring a named volume on a Pod together with exactly one source.
 *
 * Every `fromXxx` method selects a source; calling a second one replaces the first, since a volume can
 * only ever have a single source. The same set of sources is also reachable through [from] with shorter
 * method names.
 *
 * Example:
 * ```kotlin
 * addVolume("config") {
 *     fromConfigMap {
 *         name = "app-config"
 *         defaultMode = 0x1A4
 *     }
 * }
 * ```
 *
 * @constructor Creates a builder for a volume with the given name.
 * @param name The name of the volume being constructed.
 */
class VolumeSpecBuilder internal constructor(private val name: String) {
    private var source: SourceSpecBuilder<*>? = null

    /**
     * Uses the entries of a ConfigMap as the volume's content, one file per key.
     *
     * @param prepare Configures the [ConfigMapSourceSpecBuilder].
     */
    fun fromConfigMap(prepare: ConfigMapSourceSpecBuilder.() -> Unit) {
        source = ConfigMapSourceSpecBuilder().apply(prepare)
    }

    /**
     * Uses the entries of a Secret as the volume's content, one file per key.
     *
     * @param prepare Configures the [SecretSourceSpecBuilder].
     */
    fun fromSecret(prepare: SecretSourceSpecBuilder.() -> Unit) {
        source = SecretSourceSpecBuilder().apply(prepare)
    }

    /**
     * Exposes Pod metadata and container resource values as files.
     *
     * @param prepare Configures the [DownwardApiSourceSpecBuilder].
     */
    fun fromDownwardApi(prepare: DownwardApiSourceSpecBuilder.() -> Unit) {
        source = DownwardApiSourceSpecBuilder().apply(prepare)
    }

    /**
     * Combines several ConfigMap, Secret, downward API and ServiceAccount token projections into one volume.
     *
     * @param prepare Configures the [ProjectedSourceSpecBuilder].
     */
    fun fromProjected(prepare: ProjectedSourceSpecBuilder.() -> Unit) {
        source = ProjectedSourceSpecBuilder().apply(prepare)
    }

    /**
     * Uses scratch space that is created empty with the Pod and deleted with it.
     *
     * @param prepare Configures the [EmptyDirSpecBuilder].
     */
    fun emptyDir(prepare: EmptyDirSpecBuilder.() -> Unit = {}) {
        source = EmptyDirSpecBuilder().apply(prepare)
    }

    /**
     * Mounts a file or directory from the host node's filesystem.
     *
     * @param path    The absolute path on the host node.
     * @param prepare Configures the [HostPathSourceSpecBuilder].
     */
    fun fromHostPath(path: String, prepare: HostPathSourceSpecBuilder.() -> Unit = {}) {
        source = HostPathSourceSpecBuilder(path).apply(prepare)
    }

    /**
     * Mounts storage bound by an existing PersistentVolumeClaim.
     *
     * @param claimName The name of the PersistentVolumeClaim.
     * @param prepare   Configures the [PersistentVolumeClaimSourceSpecBuilder].
     */
    fun fromPersistentVolumeClaim(claimName: String, prepare: PersistentVolumeClaimSourceSpecBuilder.() -> Unit = {}) {
        source = PersistentVolumeClaimSourceSpecBuilder(claimName).apply(prepare)
    }

    /**
     * Provisions a PersistentVolumeClaim that lives and dies with the Pod.
     *
     * @param prepare Configures the [EphemeralSourceSpecBuilder].
     */
    fun fromEphemeral(prepare: EphemeralSourceSpecBuilder.() -> Unit) {
        source = EphemeralSourceSpecBuilder().apply(prepare)
    }

    /**
     * Mounts the contents of an OCI image read-only.
     *
     * @param reference The image reference.
     * @param prepare   Configures the [ImageSourceSpecBuilder].
     */
    fun fromImage(reference: String, prepare: ImageSourceSpecBuilder.() -> Unit = {}) {
        source = ImageSourceSpecBuilder(reference).apply(prepare)
    }

    /**
     * Mounts storage provided by a CSI driver.
     *
     * @param driver  The name of the CSI driver.
     * @param prepare Configures the [CsiSourceSpecBuilder].
     */
    fun fromCsi(driver: String, prepare: CsiSourceSpecBuilder.() -> Unit = {}) {
        source = CsiSourceSpecBuilder(driver).apply(prepare)
    }

    /**
     * Mounts an export of an NFS server.
     *
     * @param server  The hostname or IP address of the NFS server.
     * @param path    The absolute path of the export.
     * @param prepare Configures the [NfsSourceSpecBuilder].
     */
    fun fromNfs(server: String, path: String, prepare: NfsSourceSpecBuilder.() -> Unit = {}) {
        source = NfsSourceSpecBuilder(server, path).apply(prepare)
    }

    /**
     * Mounts an iSCSI logical unit.
     *
     * @param targetPortal The iSCSI target portal.
     * @param iqn          The iSCSI qualified name of the target.
     * @param lun          The logical unit number.
     * @param prepare      Configures the [IscsiSourceSpecBuilder].
     */
    fun fromIscsi(targetPortal: String, iqn: String, lun: Int, prepare: IscsiSourceSpecBuilder.() -> Unit = {}) {
        source = IscsiSourceSpecBuilder(targetPortal, iqn, lun).apply(prepare)
    }

    /**
     * Mounts a Fibre Channel logical unit.
     *
     * @param prepare Configures the [FibreChannelSourceSpecBuilder].
     */
    fun fromFibreChannel(prepare: FibreChannelSourceSpecBuilder.() -> Unit) {
        source = FibreChannelSourceSpecBuilder().apply(prepare)
    }

    /**
     * Mounts a Ceph RADOS block device.
     *
     * @param image   The name of the RADOS image.
     * @param prepare Configures the [RbdSourceSpecBuilder].
     */
    fun fromRbd(image: String, prepare: RbdSourceSpecBuilder.() -> Unit) {
        source = RbdSourceSpecBuilder(image).apply(prepare)
    }

    /**
     * Mounts a CephFS filesystem.
     *
     * @param prepare Configures the [CephFsSourceSpecBuilder].
     */
    fun fromCephFs(prepare: CephFsSourceSpecBuilder.() -> Unit) {
        source = CephFsSourceSpecBuilder().apply(prepare)
    }

    /**
     * Mounts a GlusterFS volume.
     *
     * @param endpoints The name of the Endpoints object describing the Gluster cluster.
     * @param path      The name of the Gluster volume.
     * @param prepare   Configures the [GlusterFsSourceSpecBuilder].
     */
    fun fromGlusterFs(endpoints: String, path: String, prepare: GlusterFsSourceSpecBuilder.() -> Unit = {}) {
        source = GlusterFsSourceSpecBuilder(endpoints, path).apply(prepare)
    }

    /**
     * Mounts an AWS Elastic Block Store volume.
     *
     * @param volumeID The identifier of the EBS volume.
     * @param prepare  Configures the [AwsElasticBlockStoreSourceSpecBuilder].
     */
    fun fromAwsElasticBlockStore(
        volumeID: String,
        prepare: AwsElasticBlockStoreSourceSpecBuilder.() -> Unit = {}
    ) {
        source = AwsElasticBlockStoreSourceSpecBuilder(volumeID).apply(prepare)
    }

    /**
     * Mounts a Google Compute Engine persistent disk.
     *
     * @param pdName  The name of the persistent disk.
     * @param prepare Configures the [GcePersistentDiskSourceSpecBuilder].
     */
    fun fromGcePersistentDisk(pdName: String, prepare: GcePersistentDiskSourceSpecBuilder.() -> Unit = {}) {
        source = GcePersistentDiskSourceSpecBuilder(pdName).apply(prepare)
    }

    /**
     * Mounts an Azure data disk.
     *
     * @param diskName The name of the data disk.
     * @param diskURI  The resource URI of the data disk.
     * @param prepare  Configures the [AzureDiskSourceSpecBuilder].
     */
    fun fromAzureDisk(diskName: String, diskURI: String, prepare: AzureDiskSourceSpecBuilder.() -> Unit = {}) {
        source = AzureDiskSourceSpecBuilder(diskName, diskURI).apply(prepare)
    }

    /**
     * Mounts an Azure Files share.
     *
     * @param secretName The Secret holding the storage account credentials.
     * @param shareName  The name of the share.
     * @param prepare    Configures the [AzureFileSourceSpecBuilder].
     */
    fun fromAzureFile(secretName: String, shareName: String, prepare: AzureFileSourceSpecBuilder.() -> Unit = {}) {
        source = AzureFileSourceSpecBuilder(secretName, shareName).apply(prepare)
    }

    /**
     * Mounts an OpenStack Cinder volume.
     *
     * @param volumeID The identifier of the Cinder volume.
     * @param prepare  Configures the [CinderSourceSpecBuilder].
     */
    fun fromCinder(volumeID: String, prepare: CinderSourceSpecBuilder.() -> Unit = {}) {
        source = CinderSourceSpecBuilder(volumeID).apply(prepare)
    }

    /**
     * Mounts a Portworx volume.
     *
     * @param volumeID The identifier of the Portworx volume.
     * @param prepare  Configures the [PortworxVolumeSourceSpecBuilder].
     */
    fun fromPortworx(volumeID: String, prepare: PortworxVolumeSourceSpecBuilder.() -> Unit = {}) {
        source = PortworxVolumeSourceSpecBuilder(volumeID).apply(prepare)
    }

    /**
     * Mounts a vSphere virtual machine disk.
     *
     * @param volumePath The datastore path of the VMDK.
     * @param prepare    Configures the [VsphereVolumeSourceSpecBuilder].
     */
    fun fromVsphereVolume(volumePath: String, prepare: VsphereVolumeSourceSpecBuilder.() -> Unit = {}) {
        source = VsphereVolumeSourceSpecBuilder(volumePath).apply(prepare)
    }

    /**
     * Clones a Git repository into the volume.
     *
     * @param repository The URL of the repository.
     * @param prepare    Configures the [GitRepoSourceSpecBuilder].
     */
    @Deprecated(
        message = "The gitRepo volume source is deprecated since Kubernetes 1.11 and disabled by default " +
                "since 1.33. Clone into an emptyDir volume from an init container instead.",
        level = DeprecationLevel.WARNING
    )
    fun fromGitRepo(repository: String, prepare: GitRepoSourceSpecBuilder.() -> Unit = {}) {
        source = GitRepoSourceSpecBuilder(repository).apply(prepare)
    }

    /**
     * Mounts storage through an out-of-tree FlexVolume driver.
     *
     * @param driver  The name of the FlexVolume driver.
     * @param prepare Configures the [FlexVolumeSourceSpecBuilder].
     */
    @Deprecated(
        message = "The FlexVolume source is deprecated since Kubernetes 1.23. Use a CSI driver instead.",
        level = DeprecationLevel.WARNING
    )
    fun fromFlexVolume(driver: String, prepare: FlexVolumeSourceSpecBuilder.() -> Unit = {}) {
        source = FlexVolumeSourceSpecBuilder(driver).apply(prepare)
    }

    /**
     * Mounts a Flocker dataset.
     *
     * @param prepare Configures the [FlockerSourceSpecBuilder].
     */
    @Deprecated(
        message = "The Flocker volume source was removed in Kubernetes 1.25 and is rejected by newer API servers.",
        level = DeprecationLevel.WARNING
    )
    fun fromFlocker(prepare: FlockerSourceSpecBuilder.() -> Unit) {
        source = FlockerSourceSpecBuilder().apply(prepare)
    }

    /**
     * Mounts a Quobyte volume.
     *
     * @param registry The Quobyte registry.
     * @param volume   The name of the Quobyte volume.
     * @param prepare  Configures the [QuobyteSourceSpecBuilder].
     */
    @Deprecated(
        message = "The Quobyte volume source was removed in Kubernetes 1.25 and is rejected by newer API servers.",
        level = DeprecationLevel.WARNING
    )
    fun fromQuobyte(registry: String, volume: String, prepare: QuobyteSourceSpecBuilder.() -> Unit = {}) {
        source = QuobyteSourceSpecBuilder(registry, volume).apply(prepare)
    }

    /**
     * Mounts a Dell EMC ScaleIO volume.
     *
     * @param gateway   The address of the ScaleIO API gateway.
     * @param system    The name of the storage system.
     * @param secretRef The name of the Secret holding the ScaleIO credentials.
     * @param prepare   Configures the [ScaleIoSourceSpecBuilder].
     */
    @Deprecated(
        message = "The ScaleIO volume source was removed in Kubernetes 1.26 and is rejected by newer API servers.",
        level = DeprecationLevel.WARNING
    )
    fun fromScaleIo(
        gateway: String,
        system: String,
        secretRef: String,
        prepare: ScaleIoSourceSpecBuilder.() -> Unit = {}
    ) {
        source = ScaleIoSourceSpecBuilder(gateway, system, secretRef).apply(prepare)
    }

    /**
     * Mounts a StorageOS volume.
     *
     * @param volumeName The name of the StorageOS volume.
     * @param prepare    Configures the [StorageOsSourceSpecBuilder].
     */
    @Deprecated(
        message = "The StorageOS volume source was removed in Kubernetes 1.25 and is rejected by newer API servers.",
        level = DeprecationLevel.WARNING
    )
    fun fromStorageOs(volumeName: String, prepare: StorageOsSourceSpecBuilder.() -> Unit = {}) {
        source = StorageOsSourceSpecBuilder(volumeName).apply(prepare)
    }

    /**
     * Mounts a Photon Controller persistent disk.
     *
     * @param pdID    The identifier of the persistent disk.
     * @param prepare Configures the [PhotonPersistentDiskSourceSpecBuilder].
     */
    @Deprecated(
        message = "The Photon Controller volume source was removed in Kubernetes 1.24 and is rejected by " +
                "newer API servers.",
        level = DeprecationLevel.WARNING
    )
    fun fromPhotonPersistentDisk(pdID: String, prepare: PhotonPersistentDiskSourceSpecBuilder.() -> Unit = {}) {
        source = PhotonPersistentDiskSourceSpecBuilder(pdID).apply(prepare)
    }

    /**
     * Configures the volume's source through short method names.
     *
     * Example:
     * ```kotlin
     * from {
     *     configMap { name = "app-config" }
     * }
     * ```
     *
     * @param prepare Configures the [FromBuilder].
     */
    fun from(prepare: FromBuilder.() -> Unit) {
        FromBuilder().apply(prepare)
    }

    /**
     * Builds the configured volume.
     *
     * @return A [VolumeSpec] carrying the volume name and its single source.
     * @throws IllegalArgumentException If no source has been configured.
     */
    internal fun build(): VolumeSpec {
        require(source != null) { "A volume source must be set for volume '$name'" }

        return VolumeSpec(name, source!!.build())
    }

    /**
     * The common contract of all volume source builders.
     *
     * @param T The concrete [SourceSpec] produced by the implementing builder.
     */
    sealed interface SourceSpecBuilder<T : SourceSpec> {
        /**
         * Builds the configured volume source.
         *
         * @return The configured source specification.
         */
        fun build(): T
    }

    /**
     * The shared configuration of the ConfigMap and Secret volume sources.
     *
     * @param T The concrete [FileSourceSpec] produced by the implementing builder.
     */
    sealed class FileSourceSpecBuilder<T : FileSourceSpec> : SourceSpecBuilder<T> {
        protected var items: MutableList<KeyToPathSpecBuilder>? = null; private set

        /**
         * The name of the referenced ConfigMap or Secret.
         */
        var name: String? = null

        /**
         * If true, the volume mounts empty instead of failing when the referenced object is missing.
         */
        var optional: Boolean? = null

        /**
         * The POSIX permissions applied to created files unless overridden per item.
         */
        var defaultMode: Int? = null

        /**
         * Projects a single key under a specific relative path instead of projecting all keys.
         *
         * @param key     The key to project.
         * @param path    The relative path the key's value is written to.
         * @param prepare Configures the [KeyToPathSpecBuilder].
         */
        fun addItem(key: String, path: String, prepare: KeyToPathSpecBuilder.() -> Unit = {}) {
            if (items == null) {
                items = mutableListOf()
            }
            items!!.add(KeyToPathSpecBuilder(key, path).apply(prepare))
        }

        /**
         * Projects several keys under specific relative paths.
         *
         * @param prepare Configures the [ItemListBuilder].
         */
        fun items(prepare: ItemListBuilder.() -> Unit) =
            ItemListBuilder().apply(prepare)

        /**
         * Builder for a single [KeyToPathSpec].
         *
         * @constructor Creates a builder for the given key and target path.
         * @param key  The key to project.
         * @param path The relative path the key's value is written to.
         */
        class KeyToPathSpecBuilder internal constructor(private val key: String, private val path: String) {
            /**
             * The POSIX permissions of the created file. Falls back to the volume's default mode when unset.
             */
            var mode: Int? = null

            /**
             * Builds the configured key mapping.
             *
             * @return A [KeyToPathSpec] carrying the configured values.
             */
            internal fun build() = KeyToPathSpec(key, path, mode)
        }

        /**
         * Collects several key mappings for a file-based volume source.
         */
        inner class ItemListBuilder internal constructor() {
            /**
             * Projects a single key under a specific relative path.
             *
             * @param key     The key to project.
             * @param path    The relative path the key's value is written to.
             * @param prepare Configures the [KeyToPathSpecBuilder].
             */
            fun item(key: String, path: String, prepare: KeyToPathSpecBuilder.() -> Unit = {}) =
                addItem(key, path, prepare)
        }
    }

    /**
     * Builder for a [ConfigMapSourceSpec].
     */
    class ConfigMapSourceSpecBuilder internal constructor() : FileSourceSpecBuilder<ConfigMapSourceSpec>() {
        /**
         * Builds the configured ConfigMap source.
         *
         * @return A [ConfigMapSourceSpec] carrying the configured values.
         */
        override fun build(): ConfigMapSourceSpec =
            ConfigMapSourceSpec(name, optional, defaultMode, items?.map { it.build() })
    }

    /**
     * Builder for a [SecretSourceSpec].
     */
    class SecretSourceSpecBuilder internal constructor() : FileSourceSpecBuilder<SecretSourceSpec>() {
        /**
         * Builds the configured Secret source.
         *
         * @return A [SecretSourceSpec] carrying the configured values.
         */
        override fun build(): SecretSourceSpec =
            SecretSourceSpec(name, optional, defaultMode, items?.map { it.build() })
    }

    /**
     * Builder for a [DownwardApiSourceSpec], exposing Pod metadata as files.
     */
    class DownwardApiSourceSpecBuilder internal constructor() : SourceSpecBuilder<DownwardApiSourceSpec> {
        private val items = mutableListOf<DownwardApiItemSpec>()

        /**
         * The POSIX permissions applied to created files unless overridden per item.
         */
        var defaultMode: Int? = null

        /**
         * Writes a field of the Pod itself into a file.
         *
         * @param path       The relative path of the created file.
         * @param fieldPath  The selected field, for example `metadata.name`.
         * @param apiVersion The API version the field path is interpreted against.
         * @param mode       The POSIX permissions of the created file.
         */
        fun addFieldRef(path: String, fieldPath: String, apiVersion: String? = null, mode: Int? = null) {
            items += DownwardApiItemSpec(path, ObjectFieldSelectorSpec(fieldPath, apiVersion), null, mode)
        }

        /**
         * Writes a resource request or limit of a container into a file.
         *
         * @param path          The relative path of the created file.
         * @param resource      The selected resource, for example `limits.cpu`.
         * @param containerName The container the resource is read from.
         * @param divisor       The unit the value is divided by, for example `1Mi`.
         * @param mode          The POSIX permissions of the created file.
         */
        fun addResourceFieldRef(
            path: String,
            resource: String,
            containerName: String,
            divisor: String? = null,
            mode: Int? = null
        ) {
            items += DownwardApiItemSpec(
                path,
                null,
                ResourceFieldSelectorSpec(resource, containerName, divisor),
                mode
            )
        }

        /**
         * Builds the configured downward API source.
         *
         * @return A [DownwardApiSourceSpec] carrying the configured values.
         */
        override fun build(): DownwardApiSourceSpec = DownwardApiSourceSpec(items.toList(), defaultMode)
    }

    /**
     * Builder for a [ProjectedSourceSpec], merging several projections into one volume.
     */
    class ProjectedSourceSpecBuilder internal constructor() : SourceSpecBuilder<ProjectedSourceSpec> {
        private val sources = mutableListOf<ProjectedSourceEntrySpec>()

        /**
         * The POSIX permissions applied to created files unless overridden per item.
         */
        var defaultMode: Int? = null

        /**
         * Adds a ConfigMap projection.
         *
         * @param prepare Configures the [ConfigMapSourceSpecBuilder].
         */
        fun addConfigMap(prepare: ConfigMapSourceSpecBuilder.() -> Unit) {
            sources += ProjectedSourceEntrySpec(
                ConfigMapSourceSpecBuilder().apply(prepare).build(), null, null, null
            )
        }

        /**
         * Adds a Secret projection.
         *
         * @param prepare Configures the [SecretSourceSpecBuilder].
         */
        fun addSecret(prepare: SecretSourceSpecBuilder.() -> Unit) {
            sources += ProjectedSourceEntrySpec(
                null, SecretSourceSpecBuilder().apply(prepare).build(), null, null
            )
        }

        /**
         * Adds a downward API projection.
         *
         * @param prepare Configures the [DownwardApiSourceSpecBuilder].
         */
        fun addDownwardApi(prepare: DownwardApiSourceSpecBuilder.() -> Unit) {
            sources += ProjectedSourceEntrySpec(
                null, null, DownwardApiSourceSpecBuilder().apply(prepare).build(), null
            )
        }

        /**
         * Adds a ServiceAccount token projection.
         *
         * @param path              The relative path the token is written to.
         * @param audience          The intended audience of the token.
         * @param expirationSeconds The requested validity of the token in seconds.
         */
        fun addServiceAccountToken(path: String, audience: String? = null, expirationSeconds: Long? = null) {
            sources += ProjectedSourceEntrySpec(
                null, null, null, ServiceAccountTokenProjectionSpec(path, audience, expirationSeconds)
            )
        }

        /**
         * Builds the configured projected source.
         *
         * @return A [ProjectedSourceSpec] carrying the configured values.
         */
        override fun build(): ProjectedSourceSpec = ProjectedSourceSpec(sources.toList(), defaultMode)
    }

    /**
     * Builder for an [EmptyDirSourceSpec].
     */
    class EmptyDirSpecBuilder internal constructor() : SourceSpecBuilder<EmptyDirSourceSpec> {
        /**
         * Where the storage is backed. Defaults to the node's disk when unset.
         */
        var medium: MediumType? = null

        /**
         * The maximum amount of storage this volume may consume.
         */
        var sizeLimit: MemoryValue? = null

        /**
         * Builds the configured emptyDir source.
         *
         * @return An [EmptyDirSourceSpec] carrying the configured values.
         */
        override fun build() = EmptyDirSourceSpec(medium, sizeLimit)
    }

    /**
     * Builder for a [HostPathSourceSpec].
     *
     * @constructor Creates a builder for the given host path.
     * @param path The absolute path on the host node.
     */
    class HostPathSourceSpecBuilder internal constructor(private val path: String) :
        SourceSpecBuilder<HostPathSourceSpec> {
        /**
         * What the path is expected to be, and whether it may be created on demand.
         */
        var type: Type? = null

        /**
         * Builds the configured hostPath source.
         *
         * @return A [HostPathSourceSpec] carrying the configured values.
         */
        override fun build(): HostPathSourceSpec = HostPathSourceSpec(path, type)
    }

    /**
     * Builder for a [PersistentVolumeClaimSourceSpec].
     *
     * @constructor Creates a builder for the given claim.
     * @param claimName The name of the PersistentVolumeClaim.
     */
    class PersistentVolumeClaimSourceSpecBuilder internal constructor(private val claimName: String) :
        SourceSpecBuilder<PersistentVolumeClaimSourceSpec> {
        /**
         * If true, the volume is mounted read-only regardless of the claim's access mode.
         */
        var readOnly: Boolean? = null

        /**
         * Builds the configured PersistentVolumeClaim source.
         *
         * @return A [PersistentVolumeClaimSourceSpec] carrying the configured values.
         */
        override fun build(): PersistentVolumeClaimSourceSpec =
            PersistentVolumeClaimSourceSpec(claimName, readOnly)
    }

    /**
     * Builder for an [EphemeralSourceSpec], provisioning a per-Pod PersistentVolumeClaim.
     */
    class EphemeralSourceSpecBuilder internal constructor() : SourceSpecBuilder<EphemeralSourceSpec> {
        private var labels: Map<String, String>? = null
        private var annotations: Map<String, String>? = null
        private var spec: VolumeClaimTemplateSpec.Spec? = null

        /**
         * Sets the labels and annotations applied to the generated claim.
         *
         * @param labels      Labels applied to the generated claim.
         * @param annotations Annotations applied to the generated claim.
         */
        fun metadata(labels: Map<String, String>? = null, annotations: Map<String, String>? = null) {
            this.labels = labels
            this.annotations = annotations
        }

        /**
         * Configures the claim specification describing the requested storage.
         *
         * @param prepare Configures the [ClaimSpecBuilder].
         */
        fun spec(prepare: ClaimSpecBuilder.() -> Unit) {
            spec = ClaimSpecBuilder().apply(prepare).build()
        }

        /**
         * Builder for the claim specification of an ephemeral volume.
         *
         * Unlike a StatefulSet's volume claim template, an ephemeral claim carries no name - it is
         * derived from the Pod and volume name - so only the storage requirements are configurable here.
         */
        class ClaimSpecBuilder internal constructor() {
            private var requests: VolumeClaimTemplateSpec.StorageResource? = null
            private var limits: VolumeClaimTemplateSpec.StorageResource? = null

            /**
             * The access modes the generated claim requests.
             */
            var accessModes: List<VolumeClaimTemplateSpec.AccessMode>? = null

            /**
             * The storage class the generated claim requests. Uses the cluster default when unset.
             */
            var storageClassName: String? = null

            /**
             * Whether the volume is consumed as a filesystem or as a raw block device.
             */
            var volumeMode: VolumeClaimTemplateSpec.VolumeMode? = null

            /**
             * The VolumeAttributesClass applying mutable QoS parameters such as IOPS or throughput.
             */
            var volumeAttributesClassName: String? = null

            /**
             * Sets the access modes the generated claim requests.
             *
             * @param modes The requested access modes.
             */
            fun accessModes(vararg modes: VolumeClaimTemplateSpec.AccessMode) {
                accessModes = modes.toList()
            }

            /**
             * Sets the amount of storage the generated claim requests.
             *
             * @param storage The requested storage size.
             */
            fun requests(storage: MemoryValue) {
                requests = VolumeClaimTemplateSpec.StorageResource(storage)
            }

            /**
             * Sets the upper bound of storage the generated claim may consume.
             *
             * @param storage The maximum storage size.
             */
            fun limits(storage: MemoryValue) {
                limits = VolumeClaimTemplateSpec.StorageResource(storage)
            }

            /**
             * Builds the configured claim specification.
             *
             * @return A [VolumeClaimTemplateSpec.Spec] carrying the configured values.
             */
            internal fun build(): VolumeClaimTemplateSpec.Spec {
                val resources = if (requests != null || limits != null) {
                    VolumeClaimTemplateSpec.ResourceRequirements(requests, limits)
                } else {
                    null
                }
                return VolumeClaimTemplateSpec.Spec(
                    accessModes = accessModes,
                    storageClassName = storageClassName,
                    volumeMode = volumeMode,
                    resources = resources,
                    selector = null,
                    volumeName = null,
                    dataSource = null,
                    dataSourceRef = null,
                    volumeAttributesClassName = volumeAttributesClassName
                )
            }
        }

        /**
         * Builds the configured ephemeral source.
         *
         * @return An [EphemeralSourceSpec] carrying the configured values.
         * @throws IllegalArgumentException If no claim specification has been configured.
         */
        override fun build(): EphemeralSourceSpec {
            require(spec != null) { "A volume claim template spec must be set for an ephemeral volume" }

            val metadata = if (labels == null && annotations == null) {
                null
            } else {
                EphemeralVolumeClaimTemplateSpec.Metadata(labels, annotations)
            }
            return EphemeralSourceSpec(EphemeralVolumeClaimTemplateSpec(metadata, spec!!))
        }
    }

    /**
     * Builder for an [ImageSourceSpec].
     *
     * @constructor Creates a builder for the given image.
     * @param reference The image reference.
     */
    class ImageSourceSpecBuilder internal constructor(private val reference: String) :
        SourceSpecBuilder<ImageSourceSpec> {
        /**
         * Controls when the image is pulled.
         */
        var pullPolicy: ContainerSpec.ImagePullPolicy? = null

        /**
         * Builds the configured image source.
         *
         * @return An [ImageSourceSpec] carrying the configured values.
         */
        override fun build(): ImageSourceSpec = ImageSourceSpec(reference, pullPolicy)
    }

    /**
     * Builder for a [CsiSourceSpec].
     *
     * @constructor Creates a builder for the given CSI driver.
     * @param driver The name of the CSI driver.
     */
    class CsiSourceSpecBuilder internal constructor(private val driver: String) : SourceSpecBuilder<CsiSourceSpec> {
        private var volumeAttributes: MutableMap<String, String>? = null

        /**
         * If true, the volume is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * The name of a Secret holding credentials the driver needs when publishing the volume.
         */
        var nodePublishSecretRef: String? = null

        /**
         * Adds a driver-specific parameter.
         *
         * @param key   The parameter name.
         * @param value The parameter value.
         */
        fun addVolumeAttribute(key: String, value: String) {
            if (volumeAttributes == null) {
                volumeAttributes = mutableMapOf()
            }
            volumeAttributes!![key] = value
        }

        /**
         * Builds the configured CSI source.
         *
         * @return A [CsiSourceSpec] carrying the configured values.
         */
        override fun build(): CsiSourceSpec = CsiSourceSpec(
            driver = driver,
            readOnly = readOnly,
            fsType = fsType,
            volumeAttributes = volumeAttributes,
            nodePublishSecretRef = nodePublishSecretRef?.let { LocalObjectReferenceSpec(it) }
        )
    }

    /**
     * Builder for an [NfsSourceSpec].
     *
     * @constructor Creates a builder for the given NFS export.
     * @param server The hostname or IP address of the NFS server.
     * @param path   The absolute path of the export.
     */
    class NfsSourceSpecBuilder internal constructor(private val server: String, private val path: String) :
        SourceSpecBuilder<NfsSourceSpec> {
        /**
         * If true, the export is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Builds the configured NFS source.
         *
         * @return An [NfsSourceSpec] carrying the configured values.
         */
        override fun build(): NfsSourceSpec = NfsSourceSpec(server, path, readOnly)
    }

    /**
     * Builder for an [IscsiSourceSpec].
     *
     * @constructor Creates a builder for the given iSCSI target.
     * @param targetPortal The iSCSI target portal.
     * @param iqn          The iSCSI qualified name of the target.
     * @param lun          The logical unit number.
     */
    class IscsiSourceSpecBuilder internal constructor(
        private val targetPortal: String,
        private val iqn: String,
        private val lun: Int
    ) : SourceSpecBuilder<IscsiSourceSpec> {
        private var portals: MutableList<String>? = null

        /**
         * The iSCSI interface name. Defaults to `default` when unset.
         */
        var iscsiInterface: String? = null

        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * If true, the logical unit is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * If true, CHAP authentication is used for target discovery.
         */
        var chapAuthDiscovery: Boolean? = null

        /**
         * If true, CHAP authentication is used for the session itself.
         */
        var chapAuthSession: Boolean? = null

        /**
         * The name of a Secret holding the CHAP credentials.
         */
        var secretRef: String? = null

        /**
         * Overrides the initiator name for this connection.
         */
        var initiatorName: String? = null

        /**
         * Adds an additional target portal for multipath access.
         *
         * @param portal The target portal to add.
         */
        fun addPortal(portal: String) {
            if (portals == null) {
                portals = mutableListOf()
            }
            portals!!.add(portal)
        }

        /**
         * Builds the configured iSCSI source.
         *
         * @return An [IscsiSourceSpec] carrying the configured values.
         */
        override fun build(): IscsiSourceSpec = IscsiSourceSpec(
            targetPortal = targetPortal,
            iqn = iqn,
            lun = lun,
            iscsiInterface = iscsiInterface,
            fsType = fsType,
            readOnly = readOnly,
            portals = portals,
            chapAuthDiscovery = chapAuthDiscovery,
            chapAuthSession = chapAuthSession,
            secretRef = secretRef?.let { LocalObjectReferenceSpec(it) },
            initiatorName = initiatorName
        )
    }

    /**
     * Builder for a [FibreChannelSourceSpec].
     */
    class FibreChannelSourceSpecBuilder internal constructor() : SourceSpecBuilder<FibreChannelSourceSpec> {
        private var targetWWNs: MutableList<String>? = null
        private var wwids: MutableList<String>? = null

        /**
         * The logical unit number to mount. Required together with target world wide names.
         */
        var lun: Int? = null

        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * If true, the logical unit is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Adds a world wide name of a target port.
         *
         * @param wwn The world wide name to add.
         */
        fun addTargetWWN(wwn: String) {
            if (targetWWNs == null) {
                targetWWNs = mutableListOf()
            }
            targetWWNs!!.add(wwn)
        }

        /**
         * Adds a world wide identifier of a volume.
         *
         * @param wwid The world wide identifier to add.
         */
        fun addWWID(wwid: String) {
            if (wwids == null) {
                wwids = mutableListOf()
            }
            wwids!!.add(wwid)
        }

        /**
         * Builds the configured Fibre Channel source.
         *
         * @return A [FibreChannelSourceSpec] carrying the configured values.
         */
        override fun build(): FibreChannelSourceSpec =
            FibreChannelSourceSpec(targetWWNs, lun, wwids, fsType, readOnly)
    }

    /**
     * Builder for an [RbdSourceSpec].
     *
     * @constructor Creates a builder for the given RADOS image.
     * @param image The name of the RADOS image.
     */
    class RbdSourceSpecBuilder internal constructor(private val image: String) : SourceSpecBuilder<RbdSourceSpec> {
        private val monitors = mutableListOf<String>()

        /**
         * The RADOS pool the image lives in. Defaults to `rbd` when unset.
         */
        var pool: String? = null

        /**
         * The RADOS user to authenticate as. Defaults to `admin` when unset.
         */
        var user: String? = null

        /**
         * The path of the keyring file on the node.
         */
        var keyring: String? = null

        /**
         * The name of a Secret holding the authentication key, taking precedence over the keyring.
         */
        var secretRef: String? = null

        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * If true, the device is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Adds the address of a Ceph monitor.
         *
         * @param monitor The monitor address to add.
         */
        fun addMonitor(monitor: String) {
            monitors += monitor
        }

        /**
         * Builds the configured RBD source.
         *
         * @return An [RbdSourceSpec] carrying the configured values.
         */
        override fun build(): RbdSourceSpec = RbdSourceSpec(
            monitors = monitors.toList(),
            image = image,
            pool = pool,
            user = user,
            keyring = keyring,
            secretRef = secretRef?.let { LocalObjectReferenceSpec(it) },
            fsType = fsType,
            readOnly = readOnly
        )
    }

    /**
     * Builder for a [CephFsSourceSpec].
     */
    class CephFsSourceSpecBuilder internal constructor() : SourceSpecBuilder<CephFsSourceSpec> {
        private val monitors = mutableListOf<String>()

        /**
         * The path within the filesystem to mount. Defaults to its root when unset.
         */
        var path: String? = null

        /**
         * The user to authenticate as. Defaults to `admin` when unset.
         */
        var user: String? = null

        /**
         * The path of the secret file on the node.
         */
        var secretFile: String? = null

        /**
         * The name of a Secret holding the authentication key, taking precedence over the secret file.
         */
        var secretRef: String? = null

        /**
         * If true, the filesystem is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Adds the address of a Ceph monitor.
         *
         * @param monitor The monitor address to add.
         */
        fun addMonitor(monitor: String) {
            monitors += monitor
        }

        /**
         * Builds the configured CephFS source.
         *
         * @return A [CephFsSourceSpec] carrying the configured values.
         */
        override fun build(): CephFsSourceSpec = CephFsSourceSpec(
            monitors = monitors.toList(),
            path = path,
            user = user,
            secretFile = secretFile,
            secretRef = secretRef?.let { LocalObjectReferenceSpec(it) },
            readOnly = readOnly
        )
    }

    /**
     * Builder for a [GlusterFsSourceSpec].
     *
     * @constructor Creates a builder for the given Gluster volume.
     * @param endpoints The name of the Endpoints object describing the Gluster cluster.
     * @param path      The name of the Gluster volume.
     */
    class GlusterFsSourceSpecBuilder internal constructor(
        private val endpoints: String,
        private val path: String
    ) : SourceSpecBuilder<GlusterFsSourceSpec> {
        /**
         * If true, the volume is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Builds the configured GlusterFS source.
         *
         * @return A [GlusterFsSourceSpec] carrying the configured values.
         */
        override fun build(): GlusterFsSourceSpec = GlusterFsSourceSpec(endpoints, path, readOnly)
    }

    /**
     * Builder for an [AwsElasticBlockStoreSourceSpec].
     *
     * @constructor Creates a builder for the given EBS volume.
     * @param volumeID The identifier of the EBS volume.
     */
    class AwsElasticBlockStoreSourceSpecBuilder internal constructor(private val volumeID: String) :
        SourceSpecBuilder<AwsElasticBlockStoreSourceSpec> {
        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * The partition of the volume to mount. Mounts the whole volume when unset.
         */
        var partition: Int? = null

        /**
         * If true, the volume is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Builds the configured EBS source.
         *
         * @return An [AwsElasticBlockStoreSourceSpec] carrying the configured values.
         */
        override fun build(): AwsElasticBlockStoreSourceSpec =
            AwsElasticBlockStoreSourceSpec(volumeID, fsType, partition, readOnly)
    }

    /**
     * Builder for a [GcePersistentDiskSourceSpec].
     *
     * @constructor Creates a builder for the given persistent disk.
     * @param pdName The name of the persistent disk.
     */
    class GcePersistentDiskSourceSpecBuilder internal constructor(private val pdName: String) :
        SourceSpecBuilder<GcePersistentDiskSourceSpec> {
        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * The partition of the disk to mount. Mounts the whole disk when unset.
         */
        var partition: Int? = null

        /**
         * If true, the disk is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Builds the configured persistent disk source.
         *
         * @return A [GcePersistentDiskSourceSpec] carrying the configured values.
         */
        override fun build(): GcePersistentDiskSourceSpec =
            GcePersistentDiskSourceSpec(pdName, fsType, partition, readOnly)
    }

    /**
     * Builder for an [AzureDiskSourceSpec].
     *
     * @constructor Creates a builder for the given Azure data disk.
     * @param diskName The name of the data disk.
     * @param diskURI  The resource URI of the data disk.
     */
    class AzureDiskSourceSpecBuilder internal constructor(
        private val diskName: String,
        private val diskURI: String
    ) : SourceSpecBuilder<AzureDiskSourceSpec> {
        /**
         * The host caching mode used for the disk.
         */
        var cachingMode: AzureDiskSourceSpec.CachingMode? = null

        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * If true, the disk is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * The storage model backing the disk.
         */
        var kind: AzureDiskSourceSpec.Kind? = null

        /**
         * Builds the configured Azure disk source.
         *
         * @return An [AzureDiskSourceSpec] carrying the configured values.
         */
        override fun build(): AzureDiskSourceSpec =
            AzureDiskSourceSpec(diskName, diskURI, cachingMode, fsType, readOnly, kind)
    }

    /**
     * Builder for an [AzureFileSourceSpec].
     *
     * @constructor Creates a builder for the given Azure Files share.
     * @param secretName The Secret holding the storage account credentials.
     * @param shareName  The name of the share.
     */
    class AzureFileSourceSpecBuilder internal constructor(
        private val secretName: String,
        private val shareName: String
    ) : SourceSpecBuilder<AzureFileSourceSpec> {
        /**
         * If true, the share is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Builds the configured Azure Files source.
         *
         * @return An [AzureFileSourceSpec] carrying the configured values.
         */
        override fun build(): AzureFileSourceSpec = AzureFileSourceSpec(secretName, shareName, readOnly)
    }

    /**
     * Builder for a [CinderSourceSpec].
     *
     * @constructor Creates a builder for the given Cinder volume.
     * @param volumeID The identifier of the Cinder volume.
     */
    class CinderSourceSpecBuilder internal constructor(private val volumeID: String) :
        SourceSpecBuilder<CinderSourceSpec> {
        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * If true, the volume is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * The name of a Secret holding the OpenStack credentials.
         */
        var secretRef: String? = null

        /**
         * Builds the configured Cinder source.
         *
         * @return A [CinderSourceSpec] carrying the configured values.
         */
        override fun build(): CinderSourceSpec =
            CinderSourceSpec(volumeID, fsType, readOnly, secretRef?.let { LocalObjectReferenceSpec(it) })
    }

    /**
     * Builder for a [PortworxVolumeSourceSpec].
     *
     * @constructor Creates a builder for the given Portworx volume.
     * @param volumeID The identifier of the Portworx volume.
     */
    class PortworxVolumeSourceSpecBuilder internal constructor(private val volumeID: String) :
        SourceSpecBuilder<PortworxVolumeSourceSpec> {
        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * If true, the volume is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Builds the configured Portworx source.
         *
         * @return A [PortworxVolumeSourceSpec] carrying the configured values.
         */
        override fun build(): PortworxVolumeSourceSpec = PortworxVolumeSourceSpec(volumeID, fsType, readOnly)
    }

    /**
     * Builder for a [VsphereVolumeSourceSpec].
     *
     * @constructor Creates a builder for the given VMDK.
     * @param volumePath The datastore path of the VMDK.
     */
    class VsphereVolumeSourceSpecBuilder internal constructor(private val volumePath: String) :
        SourceSpecBuilder<VsphereVolumeSourceSpec> {
        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * The name of the storage policy profile applied to the disk.
         */
        var storagePolicyName: String? = null

        /**
         * The identifier of the storage policy profile.
         */
        var storagePolicyID: String? = null

        /**
         * Builds the configured vSphere source.
         *
         * @return A [VsphereVolumeSourceSpec] carrying the configured values.
         */
        override fun build(): VsphereVolumeSourceSpec =
            VsphereVolumeSourceSpec(volumePath, fsType, storagePolicyName, storagePolicyID)
    }

    /**
     * Builder for a [GitRepoSourceSpec].
     *
     * @constructor Creates a builder for the given repository.
     * @param repository The URL of the repository.
     */
    class GitRepoSourceSpecBuilder internal constructor(private val repository: String) :
        SourceSpecBuilder<GitRepoSourceSpec> {
        /**
         * The commit hash to check out. Defaults to the repository's default branch when unset.
         */
        var revision: String? = null

        /**
         * The target directory relative to the volume root.
         */
        var directory: String? = null

        /**
         * Builds the configured Git repository source.
         *
         * @return A [GitRepoSourceSpec] carrying the configured values.
         */
        override fun build(): GitRepoSourceSpec = GitRepoSourceSpec(repository, revision, directory)
    }

    /**
     * Builder for a [FlexVolumeSourceSpec].
     *
     * @constructor Creates a builder for the given FlexVolume driver.
     * @param driver The name of the FlexVolume driver.
     */
    class FlexVolumeSourceSpecBuilder internal constructor(private val driver: String) :
        SourceSpecBuilder<FlexVolumeSourceSpec> {
        private var options: MutableMap<String, String>? = null

        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * The name of a Secret holding credentials passed to the driver.
         */
        var secretRef: String? = null

        /**
         * If true, the volume is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Adds a driver-specific parameter.
         *
         * @param key   The parameter name.
         * @param value The parameter value.
         */
        fun addOption(key: String, value: String) {
            if (options == null) {
                options = mutableMapOf()
            }
            options!![key] = value
        }

        /**
         * Builds the configured FlexVolume source.
         *
         * @return A [FlexVolumeSourceSpec] carrying the configured values.
         */
        override fun build(): FlexVolumeSourceSpec = FlexVolumeSourceSpec(
            driver, fsType, secretRef?.let { LocalObjectReferenceSpec(it) }, readOnly, options
        )
    }

    /**
     * Builder for a [FlockerSourceSpec].
     */
    class FlockerSourceSpecBuilder internal constructor() : SourceSpecBuilder<FlockerSourceSpec> {
        /**
         * The name of the dataset stored as metadata on the Flocker dataset.
         */
        var datasetName: String? = null

        /**
         * The unique identifier of the Flocker dataset.
         */
        var datasetUUID: String? = null

        /**
         * Builds the configured Flocker source.
         *
         * @return A [FlockerSourceSpec] carrying the configured values.
         */
        override fun build(): FlockerSourceSpec = FlockerSourceSpec(datasetName, datasetUUID)
    }

    /**
     * Builder for a [QuobyteSourceSpec].
     *
     * @constructor Creates a builder for the given Quobyte volume.
     * @param registry The Quobyte registry.
     * @param volume   The name of the Quobyte volume.
     */
    class QuobyteSourceSpecBuilder internal constructor(
        private val registry: String,
        private val volume: String
    ) : SourceSpecBuilder<QuobyteSourceSpec> {
        /**
         * If true, the volume is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * The user to map the mount to.
         */
        var user: String? = null

        /**
         * The group to map the mount to.
         */
        var group: String? = null

        /**
         * The tenant owning the volume in a multi-tenant installation.
         */
        var tenant: String? = null

        /**
         * Builds the configured Quobyte source.
         *
         * @return A [QuobyteSourceSpec] carrying the configured values.
         */
        override fun build(): QuobyteSourceSpec = QuobyteSourceSpec(registry, volume, readOnly, user, group, tenant)
    }

    /**
     * Builder for a [ScaleIoSourceSpec].
     *
     * @constructor Creates a builder for the given ScaleIO system.
     * @param gateway   The address of the ScaleIO API gateway.
     * @param system    The name of the storage system.
     * @param secretRef The name of the Secret holding the ScaleIO credentials.
     */
    class ScaleIoSourceSpecBuilder internal constructor(
        private val gateway: String,
        private val system: String,
        private val secretRef: String
    ) : SourceSpecBuilder<ScaleIoSourceSpec> {
        /**
         * If true, the gateway is contacted over TLS.
         */
        var sslEnabled: Boolean? = null

        /**
         * The name of the protection domain the storage pool belongs to.
         */
        var protectionDomain: String? = null

        /**
         * The name of the storage pool the volume lives in.
         */
        var storagePool: String? = null

        /**
         * The redundancy mode of the volume.
         */
        var storageMode: ScaleIoSourceSpec.StorageMode? = null

        /**
         * The name of the volume already created in the ScaleIO system.
         */
        var volumeName: String? = null

        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * If true, the volume is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * Builds the configured ScaleIO source.
         *
         * @return A [ScaleIoSourceSpec] carrying the configured values.
         */
        override fun build(): ScaleIoSourceSpec = ScaleIoSourceSpec(
            gateway = gateway,
            system = system,
            secretRef = LocalObjectReferenceSpec(secretRef),
            sslEnabled = sslEnabled,
            protectionDomain = protectionDomain,
            storagePool = storagePool,
            storageMode = storageMode,
            volumeName = volumeName,
            fsType = fsType,
            readOnly = readOnly
        )
    }

    /**
     * Builder for a [StorageOsSourceSpec].
     *
     * @constructor Creates a builder for the given StorageOS volume.
     * @param volumeName The name of the StorageOS volume.
     */
    class StorageOsSourceSpecBuilder internal constructor(private val volumeName: String) :
        SourceSpecBuilder<StorageOsSourceSpec> {
        /**
         * The StorageOS namespace the volume lives in.
         */
        var volumeNamespace: String? = null

        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * If true, the volume is mounted read-only.
         */
        var readOnly: Boolean? = null

        /**
         * The name of a Secret holding the StorageOS API credentials.
         */
        var secretRef: String? = null

        /**
         * Builds the configured StorageOS source.
         *
         * @return A [StorageOsSourceSpec] carrying the configured values.
         */
        override fun build(): StorageOsSourceSpec = StorageOsSourceSpec(
            volumeName, volumeNamespace, fsType, readOnly, secretRef?.let { LocalObjectReferenceSpec(it) }
        )
    }

    /**
     * Builder for a [PhotonPersistentDiskSourceSpec].
     *
     * @constructor Creates a builder for the given Photon Controller disk.
     * @param pdID The identifier of the persistent disk.
     */
    class PhotonPersistentDiskSourceSpecBuilder internal constructor(private val pdID: String) :
        SourceSpecBuilder<PhotonPersistentDiskSourceSpec> {
        /**
         * The filesystem to mount, for example `ext4`.
         */
        var fsType: String? = null

        /**
         * Builds the configured Photon Controller source.
         *
         * @return A [PhotonPersistentDiskSourceSpec] carrying the configured values.
         */
        override fun build(): PhotonPersistentDiskSourceSpec = PhotonPersistentDiskSourceSpec(pdID, fsType)
    }

    /**
     * Offers every volume source under a short method name.
     *
     * Each method delegates to the corresponding `fromXxx` method of the enclosing [VolumeSpecBuilder].
     */
    inner class FromBuilder internal constructor() {
        /**
         * Uses the entries of a ConfigMap as the volume's content.
         *
         * @param prepare Configures the [ConfigMapSourceSpecBuilder].
         */
        fun configMap(prepare: ConfigMapSourceSpecBuilder.() -> Unit) = fromConfigMap(prepare)

        /**
         * Uses the entries of a Secret as the volume's content.
         *
         * @param prepare Configures the [SecretSourceSpecBuilder].
         */
        fun secret(prepare: SecretSourceSpecBuilder.() -> Unit) = fromSecret(prepare)

        /**
         * Exposes Pod metadata and container resource values as files.
         *
         * @param prepare Configures the [DownwardApiSourceSpecBuilder].
         */
        fun downwardApi(prepare: DownwardApiSourceSpecBuilder.() -> Unit) = fromDownwardApi(prepare)

        /**
         * Combines several projections into one volume.
         *
         * @param prepare Configures the [ProjectedSourceSpecBuilder].
         */
        fun projected(prepare: ProjectedSourceSpecBuilder.() -> Unit) = fromProjected(prepare)

        /**
         * Mounts a file or directory from the host node's filesystem.
         *
         * @param path    The absolute path on the host node.
         * @param prepare Configures the [HostPathSourceSpecBuilder].
         */
        fun hostPath(path: String, prepare: HostPathSourceSpecBuilder.() -> Unit = {}) = fromHostPath(path, prepare)

        /**
         * Mounts storage bound by an existing PersistentVolumeClaim.
         *
         * @param claimName The name of the PersistentVolumeClaim.
         * @param prepare   Configures the [PersistentVolumeClaimSourceSpecBuilder].
         */
        fun persistentVolumeClaim(claimName: String, prepare: PersistentVolumeClaimSourceSpecBuilder.() -> Unit = {}) =
            fromPersistentVolumeClaim(claimName, prepare)

        /**
         * Provisions a PersistentVolumeClaim that lives and dies with the Pod.
         *
         * @param prepare Configures the [EphemeralSourceSpecBuilder].
         */
        fun ephemeral(prepare: EphemeralSourceSpecBuilder.() -> Unit) = fromEphemeral(prepare)

        /**
         * Mounts the contents of an OCI image read-only.
         *
         * @param reference The image reference.
         * @param prepare   Configures the [ImageSourceSpecBuilder].
         */
        fun image(reference: String, prepare: ImageSourceSpecBuilder.() -> Unit = {}) = fromImage(reference, prepare)

        /**
         * Mounts storage provided by a CSI driver.
         *
         * @param driver  The name of the CSI driver.
         * @param prepare Configures the [CsiSourceSpecBuilder].
         */
        fun csi(driver: String, prepare: CsiSourceSpecBuilder.() -> Unit = {}) = fromCsi(driver, prepare)

        /**
         * Mounts an export of an NFS server.
         *
         * @param server  The hostname or IP address of the NFS server.
         * @param path    The absolute path of the export.
         * @param prepare Configures the [NfsSourceSpecBuilder].
         */
        fun nfs(server: String, path: String, prepare: NfsSourceSpecBuilder.() -> Unit = {}) =
            fromNfs(server, path, prepare)

        /**
         * Mounts an iSCSI logical unit.
         *
         * @param targetPortal The iSCSI target portal.
         * @param iqn          The iSCSI qualified name of the target.
         * @param lun          The logical unit number.
         * @param prepare      Configures the [IscsiSourceSpecBuilder].
         */
        fun iscsi(targetPortal: String, iqn: String, lun: Int, prepare: IscsiSourceSpecBuilder.() -> Unit = {}) =
            fromIscsi(targetPortal, iqn, lun, prepare)

        /**
         * Mounts a Fibre Channel logical unit.
         *
         * @param prepare Configures the [FibreChannelSourceSpecBuilder].
         */
        fun fibreChannel(prepare: FibreChannelSourceSpecBuilder.() -> Unit) = fromFibreChannel(prepare)

        /**
         * Mounts a Ceph RADOS block device.
         *
         * @param image   The name of the RADOS image.
         * @param prepare Configures the [RbdSourceSpecBuilder].
         */
        fun rbd(image: String, prepare: RbdSourceSpecBuilder.() -> Unit) = fromRbd(image, prepare)

        /**
         * Mounts a CephFS filesystem.
         *
         * @param prepare Configures the [CephFsSourceSpecBuilder].
         */
        fun cephFs(prepare: CephFsSourceSpecBuilder.() -> Unit) = fromCephFs(prepare)

        /**
         * Mounts a GlusterFS volume.
         *
         * @param endpoints The name of the Endpoints object describing the Gluster cluster.
         * @param path      The name of the Gluster volume.
         * @param prepare   Configures the [GlusterFsSourceSpecBuilder].
         */
        fun glusterFs(endpoints: String, path: String, prepare: GlusterFsSourceSpecBuilder.() -> Unit = {}) =
            fromGlusterFs(endpoints, path, prepare)

        /**
         * Mounts an AWS Elastic Block Store volume.
         *
         * @param volumeID The identifier of the EBS volume.
         * @param prepare  Configures the [AwsElasticBlockStoreSourceSpecBuilder].
         */
        fun awsElasticBlockStore(volumeID: String, prepare: AwsElasticBlockStoreSourceSpecBuilder.() -> Unit = {}) =
            fromAwsElasticBlockStore(volumeID, prepare)

        /**
         * Mounts a Google Compute Engine persistent disk.
         *
         * @param pdName  The name of the persistent disk.
         * @param prepare Configures the [GcePersistentDiskSourceSpecBuilder].
         */
        fun gcePersistentDisk(pdName: String, prepare: GcePersistentDiskSourceSpecBuilder.() -> Unit = {}) =
            fromGcePersistentDisk(pdName, prepare)

        /**
         * Mounts an Azure data disk.
         *
         * @param diskName The name of the data disk.
         * @param diskURI  The resource URI of the data disk.
         * @param prepare  Configures the [AzureDiskSourceSpecBuilder].
         */
        fun azureDisk(diskName: String, diskURI: String, prepare: AzureDiskSourceSpecBuilder.() -> Unit = {}) =
            fromAzureDisk(diskName, diskURI, prepare)

        /**
         * Mounts an Azure Files share.
         *
         * @param secretName The Secret holding the storage account credentials.
         * @param shareName  The name of the share.
         * @param prepare    Configures the [AzureFileSourceSpecBuilder].
         */
        fun azureFile(secretName: String, shareName: String, prepare: AzureFileSourceSpecBuilder.() -> Unit = {}) =
            fromAzureFile(secretName, shareName, prepare)

        /**
         * Mounts an OpenStack Cinder volume.
         *
         * @param volumeID The identifier of the Cinder volume.
         * @param prepare  Configures the [CinderSourceSpecBuilder].
         */
        fun cinder(volumeID: String, prepare: CinderSourceSpecBuilder.() -> Unit = {}) =
            fromCinder(volumeID, prepare)

        /**
         * Mounts a Portworx volume.
         *
         * @param volumeID The identifier of the Portworx volume.
         * @param prepare  Configures the [PortworxVolumeSourceSpecBuilder].
         */
        fun portworx(volumeID: String, prepare: PortworxVolumeSourceSpecBuilder.() -> Unit = {}) =
            fromPortworx(volumeID, prepare)

        /**
         * Mounts a vSphere virtual machine disk.
         *
         * @param volumePath The datastore path of the VMDK.
         * @param prepare    Configures the [VsphereVolumeSourceSpecBuilder].
         */
        fun vsphereVolume(volumePath: String, prepare: VsphereVolumeSourceSpecBuilder.() -> Unit = {}) =
            fromVsphereVolume(volumePath, prepare)
    }
}
