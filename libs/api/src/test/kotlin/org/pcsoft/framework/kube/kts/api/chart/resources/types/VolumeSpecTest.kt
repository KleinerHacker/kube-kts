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

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.types.miBytes
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertIs

class VolumeSpecTest {

    @Test
    fun testConfigMapSourceMaxContent() {
        val spec = buildConfigMapMaxSpec()

        assertEquals("config-volume", spec.name)
        val source = assertIs<VolumeSpec.ConfigMapSourceSpec>(spec.source)
        assertMaxFileSource(source, "app-config", "application.yaml", "config/application.yaml")
    }

    @Test
    fun testConfigMapSourceMaxYaml() {
        assertYaml(
            """{
              |  "name": "config-volume",
              |  "configMap": {
              |    "name": "app-config",
              |    "optional": true,
              |    "defaultMode": 420,
              |    "items": [{
              |      "key": "application.yaml",
              |      "path": "config/application.yaml",
              |      "mode": 384
              |    }]
              |  }
              |}""".trimMargin(),
            buildConfigMapMaxSpec()
        )
    }

    @Test
    fun testConfigMapSourceMinContent() {
        val spec = buildConfigMapMinSpec()

        assertEquals("config-volume", spec.name)
        val source = assertIs<VolumeSpec.ConfigMapSourceSpec>(spec.source)
        assertMinFileSource(source)
    }

    @Test
    fun testConfigMapSourceMinYaml() {
        assertYaml(
            """{
              |  "name": "config-volume",
              |  "configMap": {}
              |}""".trimMargin(),
            buildConfigMapMinSpec()
        )
    }

    @Test
    fun testSecretSourceMaxContent() {
        val spec = buildSecretMaxSpec()

        assertEquals("secret-volume", spec.name)
        val source = assertIs<VolumeSpec.SecretSourceSpec>(spec.source)
        assertMaxFileSource(source, "app-secret", "password", "secret/password")
    }

    @Test
    fun testSecretSourceMaxYaml() {
        assertYaml(
            """{
              |  "name": "secret-volume",
              |  "secret": {
              |    "secretName": "app-secret",
              |    "optional": true,
              |    "defaultMode": 420,
              |    "items": [{
              |      "key": "password",
              |      "path": "secret/password",
              |      "mode": 384
              |    }]
              |  }
              |}""".trimMargin(),
            buildSecretMaxSpec()
        )
    }

    @Test
    fun testSecretSourceMinContent() {
        val spec = buildSecretMinSpec()

        assertEquals("secret-volume", spec.name)
        val source = assertIs<VolumeSpec.SecretSourceSpec>(spec.source)
        assertMinFileSource(source)
    }

    @Test
    fun testSecretSourceMinYaml() {
        assertYaml(
            """{
              |  "name": "secret-volume",
              |  "secret": {}
              |}""".trimMargin(),
            buildSecretMinSpec()
        )
    }

    @Test
    fun testPersistentVolumeClaimSourceMaxContent() {
        val spec = buildPersistentVolumeClaimMaxSpec()

        assertEquals("pvc-volume", spec.name)
        val source = assertIs<VolumeSpec.PersistentVolumeClaimSourceSpec>(spec.source)
        assertEquals("app-pvc", source.claimName)
        assertEquals(true, source.readOnly)
    }

    @Test
    fun testPersistentVolumeClaimSourceMaxYaml() {
        assertYaml(
            """{
              |  "name": "pvc-volume",
              |  "persistentVolumeClaim": {
              |    "claimName": "app-pvc",
              |    "readOnly": true
              |  }
              |}""".trimMargin(),
            buildPersistentVolumeClaimMaxSpec()
        )
    }

    @Test
    fun testPersistentVolumeClaimSourceMinContent() {
        val spec = buildPersistentVolumeClaimMinSpec()

        assertEquals("pvc-volume", spec.name)
        val source = assertIs<VolumeSpec.PersistentVolumeClaimSourceSpec>(spec.source)
        assertEquals("app-pvc", source.claimName)
        assertEquals(null, source.readOnly)
    }

    @Test
    fun testPersistentVolumeClaimSourceMinYaml() {
        assertYaml(
            """{
              |  "name": "pvc-volume",
              |  "persistentVolumeClaim": {
              |    "claimName": "app-pvc"
              |  }
              |}""".trimMargin(),
            buildPersistentVolumeClaimMinSpec()
        )
    }

    @Test
    fun testHostPathSourceMaxContent() {
        val spec = buildHostPathMaxSpec()

        assertEquals("host-volume", spec.name)
        val source = assertIs<VolumeSpec.HostPathSourceSpec>(spec.source)
        assertEquals("/data", source.path)
        assertEquals(VolumeSpec.HostPathSourceSpec.Type.DirectoryOrCreate, source.type)
    }

    @Test
    fun testHostPathSourceMaxYaml() {
        assertYaml(
            """{
              |  "name": "host-volume",
              |  "hostPath": {
              |    "path": "/data",
              |    "type": "DirectoryOrCreate"
              |  }
              |}""".trimMargin(),
            buildHostPathMaxSpec()
        )
    }

    @Test
    fun testHostPathSourceMinContent() {
        val spec = buildHostPathMinSpec()

        assertEquals("host-volume", spec.name)
        val source = assertIs<VolumeSpec.HostPathSourceSpec>(spec.source)
        assertEquals("/data", source.path)
        assertEquals(null, source.type)
    }

    @Test
    fun testHostPathSourceMinYaml() {
        assertYaml(
            """{
              |  "name": "host-volume",
              |  "hostPath": {
              |    "path": "/data"
              |  }
              |}""".trimMargin(),
            buildHostPathMinSpec()
        )
    }

    @Test
    fun testEmptyDirSourceMaxContent() {
        val spec = buildEmptyDirMaxSpec()

        assertEquals("empty-volume", spec.name)
        val source = assertIs<VolumeSpec.EmptyDirSourceSpec>(spec.source)
        assertEquals(VolumeSpec.EmptyDirSourceSpec.MediumType.Memory, source.medium)
        assertEquals(64.miBytes, source.sizeLimit)
    }

    @Test
    fun testEmptyDirSourceMaxYaml() {
        assertYaml(
            """{
              |  "name": "empty-volume",
              |  "emptyDir": {
              |    "medium": "Memory",
              |    "sizeLimit": "64Mi"
              |  }
              |}""".trimMargin(),
            buildEmptyDirMaxSpec()
        )
    }

    @Test
    fun testEmptyDirSourceMinContent() {
        val spec = buildEmptyDirMinSpec()

        assertEquals("empty-volume", spec.name)
        val source = assertIs<VolumeSpec.EmptyDirSourceSpec>(spec.source)
        assertEquals(null, source.medium)
        assertEquals(null, source.sizeLimit)
    }

    @Test
    fun testEmptyDirSourceMinYaml() {
        assertYaml(
            """{
              |  "name": "empty-volume",
              |  "emptyDir": {}
              |}""".trimMargin(),
            buildEmptyDirMinSpec()
        )
    }

    private fun buildConfigMapMaxSpec() = VolumeSpecBuilder("config-volume").apply {
        from {
            configMap {
                name = "app-config"
                optional = true
                defaultMode = 420
                items {
                    item("application.yaml", "config/application.yaml") {
                        mode = 384
                    }
                }
            }
        }
    }.build()

    private fun buildConfigMapMinSpec() = VolumeSpecBuilder("config-volume").apply {
        from {
            configMap {}
        }
    }.build()

    private fun buildSecretMaxSpec() = VolumeSpecBuilder("secret-volume").apply {
        from {
            secret {
                name = "app-secret"
                optional = true
                defaultMode = 420
                items {
                    item("password", "secret/password") {
                        mode = 384
                    }
                }
            }
        }
    }.build()

    private fun buildSecretMinSpec() = VolumeSpecBuilder("secret-volume").apply {
        from {
            secret {}
        }
    }.build()

    private fun buildPersistentVolumeClaimMaxSpec() = VolumeSpecBuilder("pvc-volume").apply {
        from {
            persistentVolumeClaim("app-pvc") {
                readOnly = true
            }
        }
    }.build()

    private fun buildPersistentVolumeClaimMinSpec() = VolumeSpecBuilder("pvc-volume").apply {
        from {
            persistentVolumeClaim("app-pvc")
        }
    }.build()

    private fun buildHostPathMaxSpec() = VolumeSpecBuilder("host-volume").apply {
        from {
            hostPath("/data") {
                type = VolumeSpec.HostPathSourceSpec.Type.DirectoryOrCreate
            }
        }
    }.build()

    private fun buildHostPathMinSpec() = VolumeSpecBuilder("host-volume").apply {
        from {
            hostPath("/data")
        }
    }.build()

    private fun buildEmptyDirMaxSpec() = VolumeSpecBuilder("empty-volume").apply {
        emptyDir {
            medium = VolumeSpec.EmptyDirSourceSpec.MediumType.Memory
            sizeLimit = 64.miBytes
        }
    }.build()

    private fun buildEmptyDirMinSpec() = VolumeSpecBuilder("empty-volume").apply {
        emptyDir {}
    }.build()

    private fun assertMaxFileSource(source: VolumeSpec.FileSourceSpec, name: String, key: String, path: String) {
        assertEquals(name, source.name)
        assertEquals(true, source.optional)
        assertEquals(420, source.defaultMode)
        assertEquals(1, source.items?.size)
        val item = assertIs<VolumeSpec.FileSourceSpec.KeyToPathSpec>(source.items?.single())
        assertEquals(key, item.key)
        assertEquals(path, item.path)
        assertEquals(384, item.mode)
    }

    private fun assertMinFileSource(source: VolumeSpec.FileSourceSpec) {
        assertEquals(null, source.name)
        assertEquals(null, source.optional)
        assertEquals(null, source.defaultMode)
        assertEquals(null, source.items)
    }

    private fun assertYaml(expectedJson: String, spec: VolumeSpec) {
        JSONAssert.assertEquals(expectedJson, spec.toJson(), JSONCompareMode.LENIENT)
    }
}