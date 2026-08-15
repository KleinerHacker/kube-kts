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

package org.pcsoft.framework.kube.kts.api.intern.jackson

import org.pcsoft.framework.kube.kts.api.chart.resources.types.*
import org.pcsoft.framework.kube.kts.api.intern.utils.writeObject
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.*

/**
 * Maps every volume source implementation onto the YAML property name Kubernetes expects for it.
 *
 * The mapping is the single source of truth shared by [VolumeSpecSerializer] and [VolumeSpecDeserializer],
 * which keeps writing and reading a volume symmetrical.
 */
internal object VolumeSourceNames {

    /**
     * All supported volume sources, paired with their YAML property name and the class they deserialize into.
     *
     * The order matters for deserialization: the first entry whose property is present on the node wins.
     */
    val ENTRIES: List<Pair<String, Class<out VolumeSpec.SourceSpec>>> = listOf(
        "configMap" to ConfigMapSourceSpec::class.java,
        "secret" to SecretSourceSpec::class.java,
        "downwardAPI" to DownwardApiSourceSpec::class.java,
        "projected" to ProjectedSourceSpec::class.java,
        "emptyDir" to EmptyDirSourceSpec::class.java,
        "hostPath" to HostPathSourceSpec::class.java,
        "persistentVolumeClaim" to PersistentVolumeClaimSourceSpec::class.java,
        "ephemeral" to EphemeralSourceSpec::class.java,
        "image" to ImageSourceSpec::class.java,
        "csi" to CsiSourceSpec::class.java,
        "nfs" to NfsSourceSpec::class.java,
        "iscsi" to IscsiSourceSpec::class.java,
        "fc" to FibreChannelSourceSpec::class.java,
        "rbd" to RbdSourceSpec::class.java,
        "cephfs" to CephFsSourceSpec::class.java,
        "glusterfs" to GlusterFsSourceSpec::class.java,
        "awsElasticBlockStore" to AwsElasticBlockStoreSourceSpec::class.java,
        "gcePersistentDisk" to GcePersistentDiskSourceSpec::class.java,
        "azureDisk" to AzureDiskSourceSpec::class.java,
        "azureFile" to AzureFileSourceSpec::class.java,
        "cinder" to CinderSourceSpec::class.java,
        "portworxVolume" to PortworxVolumeSourceSpec::class.java,
        "vsphereVolume" to VsphereVolumeSourceSpec::class.java,
        "gitRepo" to GitRepoSourceSpec::class.java,
        "flexVolume" to FlexVolumeSourceSpec::class.java,
        "flocker" to FlockerSourceSpec::class.java,
        "quobyte" to QuobyteSourceSpec::class.java,
        "scaleIO" to ScaleIoSourceSpec::class.java,
        "storageos" to StorageOsSourceSpec::class.java,
        "photonPersistentDisk" to PhotonPersistentDiskSourceSpec::class.java,
    )

    /**
     * Resolves the YAML property name a given volume source is written under.
     *
     * The `when` is exhaustive over the sealed [VolumeSpec.SourceSpec] hierarchy, so adding a new source
     * without registering it here is a compile error rather than a silently malformed manifest.
     *
     * @param source The volume source to resolve.
     * @return The Kubernetes property name for this source.
     */
    fun nameOf(source: VolumeSpec.SourceSpec): String = when (source) {
        is ConfigMapSourceSpec -> "configMap"
        is SecretSourceSpec -> "secret"
        is DownwardApiSourceSpec -> "downwardAPI"
        is ProjectedSourceSpec -> "projected"
        is EmptyDirSourceSpec -> "emptyDir"
        is HostPathSourceSpec -> "hostPath"
        is PersistentVolumeClaimSourceSpec -> "persistentVolumeClaim"
        is EphemeralSourceSpec -> "ephemeral"
        is ImageSourceSpec -> "image"
        is CsiSourceSpec -> "csi"
        is NfsSourceSpec -> "nfs"
        is IscsiSourceSpec -> "iscsi"
        is FibreChannelSourceSpec -> "fc"
        is RbdSourceSpec -> "rbd"
        is CephFsSourceSpec -> "cephfs"
        is GlusterFsSourceSpec -> "glusterfs"
        is AwsElasticBlockStoreSourceSpec -> "awsElasticBlockStore"
        is GcePersistentDiskSourceSpec -> "gcePersistentDisk"
        is AzureDiskSourceSpec -> "azureDisk"
        is AzureFileSourceSpec -> "azureFile"
        is CinderSourceSpec -> "cinder"
        is PortworxVolumeSourceSpec -> "portworxVolume"
        is VsphereVolumeSourceSpec -> "vsphereVolume"
        is GitRepoSourceSpec -> "gitRepo"
        is FlexVolumeSourceSpec -> "flexVolume"
        is FlockerSourceSpec -> "flocker"
        is QuobyteSourceSpec -> "quobyte"
        is ScaleIoSourceSpec -> "scaleIO"
        is StorageOsSourceSpec -> "storageos"
        is PhotonPersistentDiskSourceSpec -> "photonPersistentDisk"
    }
}

/**
 * Serializes a [VolumeSpec] into the flat shape Kubernetes expects.
 *
 * A volume carries its source in a dedicated property, but Kubernetes renders the source as a sibling of
 * `name` whose key names the source type. This serializer performs that flattening, writing `name`
 * followed by exactly one source property resolved through [VolumeSourceNames].
 */
class VolumeSpecSerializer : ValueSerializer<VolumeSpec>() {
    override fun serialize(
        value: VolumeSpec?,
        gen: JsonGenerator,
        ctxt: SerializationContext
    ) {
        if (value == null) {
            gen.writeNull()
            return
        }

        gen.writeObject {
            gen.writeStringProperty("name", value.name)
            gen.writePOJOProperty(VolumeSourceNames.nameOf(value.source), value.source)
        }
    }
}

/**
 * Deserializes the flat Kubernetes representation of a volume back into a [VolumeSpec].
 *
 * The source type is recovered by looking for the first property registered in [VolumeSourceNames] that
 * is present on the node.
 */
class VolumeSpecDeserializer : ValueDeserializer<VolumeSpec>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): VolumeSpec {
        val node: JsonNode = p.readValueAsTree()
        val name = node.get("name")?.asString()
            ?: throw IllegalArgumentException("Volume is missing the required 'name' property")

        val entry = VolumeSourceNames.ENTRIES.firstOrNull { (property, _) -> node.has(property) }
            ?: throw IllegalArgumentException(
                "Volume '$name' does not declare any known volume source. Expected one of: " +
                        VolumeSourceNames.ENTRIES.joinToString { it.first }
            )

        val source = ctxt.readTreeAsValue(node.get(entry.first), entry.second)
        return VolumeSpec(name, source)
    }
}
