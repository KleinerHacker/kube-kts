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

import com.fasterxml.jackson.annotation.JsonProperty
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Maps a single key of a ConfigMap or Secret onto a relative path inside the mounted volume.
 *
 * @property key  The key to project.
 * @property path The relative path the key's value is written to. Must not be absolute and must not
 *                contain a `..` segment.
 * @property mode The POSIX permissions of the created file, given as a decimal or octal integer between
 *                0 and 0777. Falls back to the volume's default mode if unset.
 */
@NoArgs
data class KeyToPathSpec(
    val key: String,
    val path: String,
    val mode: Int?
) {
    /**
     * Validates the key, the relative target path and the file mode.
     */
    init {
        require(key.isNotBlank()) { "Key must not be blank" }
        require(path.isNotBlank()) { "Path must not be blank" }
        require(!path.startsWith('/')) { "Path must be relative, but was '$path'" }
        require(".." !in path.split('/')) { "Path must not contain a '..' segment, but was '$path'" }
        mode?.let { require(it in 0..511) { "Mode must be between 0 and 0777, but was $it" } }
    }
}

/**
 * The shared shape of the ConfigMap and Secret volume sources.
 *
 * Both project a set of keys as files into the volume, optionally restricting and renaming them via
 * [items] and controlling the resulting file permissions via [defaultMode].
 *
 * @property name        The name of the referenced ConfigMap or Secret.
 * @property optional    If true, the volume mounts empty instead of failing when the referenced object
 *                       does not exist.
 * @property defaultMode The POSIX permissions applied to created files unless overridden per item.
 * @property items       The subset of keys to project. If unset, every key is projected under its own name.
 */
@NoArgs
sealed class FileSourceSpec(
    val name: String?,
    val optional: Boolean?,
    val defaultMode: Int?,
    val items: List<KeyToPathSpec>?
) : VolumeSpec.SourceSpec {
    /**
     * Validates the referenced name and the default file mode.
     */
    init {
        name?.let { require(it.isNotBlank()) { "Referenced object name must not be blank" } }
        defaultMode?.let { require(it in 0..511) { "Default mode must be between 0 and 0777, but was $it" } }
    }
}

/**
 * Projects the entries of a ConfigMap into a volume, one file per key.
 *
 * @constructor Creates a ConfigMap volume source.
 * @param name        The name of the referenced ConfigMap.
 * @param optional    If true, the volume mounts empty when the ConfigMap is missing.
 * @param defaultMode The POSIX permissions applied to created files.
 * @param items       The subset of keys to project.
 */
@NoArgs
class ConfigMapSourceSpec(
    name: String?,
    optional: Boolean?,
    defaultMode: Int?,
    items: List<KeyToPathSpec>?
) : FileSourceSpec(name, optional, defaultMode, items)

/**
 * Projects the entries of a Secret into a volume, one file per key.
 *
 * Note that the Secret's name is rendered under the key `secretName` rather than `name`, matching the
 * Kubernetes schema for this source.
 *
 * @constructor Creates a Secret volume source.
 * @param name        The name of the referenced Secret.
 * @param optional    If true, the volume mounts empty when the Secret is missing.
 * @param defaultMode The POSIX permissions applied to created files.
 * @param items       The subset of keys to project.
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
 * Selects a field of the Pod and exposes its value.
 *
 * @property fieldPath  The path of the selected field, for example `metadata.name` or
 *                      `metadata.labels['app']`.
 * @property apiVersion The API version the [fieldPath] is interpreted against. Defaults to `v1`.
 */
@NoArgs
data class ObjectFieldSelectorSpec(
    val fieldPath: String,
    val apiVersion: String?
) {
    /**
     * Validates that the selected field path is not blank.
     */
    init {
        require(fieldPath.isNotBlank()) { "Field path must not be blank" }
    }
}

/**
 * Selects a resource request or limit of a container and exposes its value.
 *
 * @property resource      The selected resource, for example `limits.cpu` or `requests.memory`.
 * @property containerName The container the resource is read from. Required in the downward API of a
 *                         volume, optional in an environment variable reference.
 * @property divisor       The unit the exposed value is divided by, for example `1m` for CPU or `1Mi`
 *                         for memory. Defaults to `1`.
 */
@NoArgs
data class ResourceFieldSelectorSpec(
    val resource: String,
    val containerName: String?,
    val divisor: String?
) {
    /**
     * Validates the selected resource and the referenced container name.
     */
    init {
        require(resource.isNotBlank()) { "Resource must not be blank" }
        containerName?.let { require(it.isNotBlank()) { "Container name must not be blank" } }
    }
}

/**
 * Writes a single piece of Pod metadata into a file of a downward API volume.
 *
 * Exactly one of [fieldRef] and [resourceFieldRef] has to be set.
 *
 * @property path             The relative path of the created file.
 * @property fieldRef         Selects a field of the Pod itself.
 * @property resourceFieldRef Selects a resource request or limit of one of the Pod's containers.
 * @property mode             The POSIX permissions of the created file.
 */
@NoArgs
data class DownwardApiItemSpec(
    val path: String,
    val fieldRef: ObjectFieldSelectorSpec?,
    val resourceFieldRef: ResourceFieldSelectorSpec?,
    val mode: Int?
) {
    /**
     * Validates the target path, the file mode and that exactly one selector is given.
     */
    init {
        require(path.isNotBlank()) { "Path must not be blank" }
        require(!path.startsWith('/')) { "Path must be relative, but was '$path'" }
        require(".." !in path.split('/')) { "Path must not contain a '..' segment, but was '$path'" }
        require((fieldRef == null) != (resourceFieldRef == null)) {
            "Exactly one of 'fieldRef' and 'resourceFieldRef' must be set"
        }
        mode?.let { require(it in 0..511) { "Mode must be between 0 and 0777, but was $it" } }
    }
}

/**
 * Exposes Pod metadata - labels, annotations, name, namespace or container resource values - as files.
 *
 * @property items       The metadata entries to expose, each as its own file.
 * @property defaultMode The POSIX permissions applied to created files unless overridden per item.
 */
@NoArgs
data class DownwardApiSourceSpec(
    val items: List<DownwardApiItemSpec>,
    val defaultMode: Int?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that at least one item is present and that the default mode is in range.
     */
    init {
        require(items.isNotEmpty()) { "At least one downward API item is required" }
        defaultMode?.let { require(it in 0..511) { "Default mode must be between 0 and 0777, but was $it" } }
    }
}

/**
 * Requests a short-lived, automatically rotated ServiceAccount token as a file.
 *
 * @property path              The relative path the token is written to.
 * @property audience          The intended audience of the token. Defaults to the API server.
 * @property expirationSeconds The requested validity of the token in seconds. The API server may return
 *                             a shorter lifetime.
 */
@NoArgs
data class ServiceAccountTokenProjectionSpec(
    val path: String,
    val audience: String?,
    val expirationSeconds: Long?
) {
    /**
     * Validates the target path and the requested token lifetime.
     */
    init {
        require(path.isNotBlank()) { "Path must not be blank" }
        require(!path.startsWith('/')) { "Path must be relative, but was '$path'" }
        expirationSeconds?.let { require(it > 0) { "Expiration must be positive, but was $it" } }
    }
}

/**
 * A single contributor to a projected volume.
 *
 * Exactly one of the properties has to be set; the chosen one determines what is projected.
 *
 * @property configMap           Projects keys of a ConfigMap.
 * @property secret              Projects keys of a Secret.
 * @property downwardAPI         Projects Pod metadata.
 * @property serviceAccountToken Projects a ServiceAccount token.
 */
@NoArgs
data class ProjectedSourceEntrySpec(
    val configMap: ConfigMapSourceSpec?,
    val secret: SecretSourceSpec?,
    val downwardAPI: DownwardApiSourceSpec?,
    val serviceAccountToken: ServiceAccountTokenProjectionSpec?
) {
    /**
     * Validates that exactly one projection is configured.
     */
    init {
        val count = listOfNotNull(configMap, secret, downwardAPI, serviceAccountToken).size
        require(count == 1) {
            "Exactly one of 'configMap', 'secret', 'downwardAPI' and 'serviceAccountToken' must be set, but $count were"
        }
    }
}

/**
 * Combines several ConfigMap, Secret, downward API and ServiceAccount token projections into one volume.
 *
 * A projected volume lets a container consume data from multiple sources through a single mount point,
 * which avoids having to mount each source separately.
 *
 * @property sources     The projections merged into this volume, applied in order.
 * @property defaultMode The POSIX permissions applied to created files unless overridden per item.
 */
@NoArgs
data class ProjectedSourceSpec(
    val sources: List<ProjectedSourceEntrySpec>,
    val defaultMode: Int?
) : VolumeSpec.SourceSpec {
    /**
     * Validates that at least one projection is present and that the default mode is in range.
     */
    init {
        require(sources.isNotEmpty()) { "At least one projected source is required" }
        defaultMode?.let { require(it in 0..511) { "Default mode must be between 0 and 0777, but was $it" } }
    }
}
