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
     * @param prepare Configures the [EmptyDirSourceSpecBuilder].
     */
    fun emptyDir(prepare: EmptyDirSourceSpecBuilder.() -> Unit = {}) {
        source = EmptyDirSourceSpecBuilder().apply(prepare)
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
