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
    fun testEmptyDirContent() {
        val spec = VolumeSpecBuilder("volume").apply {
            emptyDir {
                medium = VolumeSpec.EmptyDirSourceSpec.MediumType.Memory
                sizeLimit = 64.miBytes
            }
        }.build()

        assertEquals("volume", spec.name)
        val source = assertIs<VolumeSpec.EmptyDirSourceSpec>(spec.source)
        assertEquals(VolumeSpec.EmptyDirSourceSpec.MediumType.Memory, source.medium)
        assertEquals(64.miBytes, source.sizeLimit)
    }

    @Test
    fun testEmptyDirYaml() {
        val actualJson = VolumeSpecBuilder("volume").apply {
            emptyDir {
                medium = VolumeSpec.EmptyDirSourceSpec.MediumType.Memory
                sizeLimit = 64.miBytes
            }
        }.build().toJson()
        val expectedJson = """{
          |  "name": "volume",
          |  "emptyDir": {
          |    "medium": "Memory",
          |    "sizeLimit": "64Mi"
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testConfigMapSourceContent() {
        val spec = VolumeSpecBuilder("config-volume").apply {
            from {
                configMap("app-config")
            }
        }.build()

        assertEquals("config-volume", spec.name)
        val source = assertIs<VolumeSpec.ConfigMapSourceSpec>(spec.source)
        assertEquals("app-config", source.name)
    }

    @Test
    fun testConfigMapSourceYaml() {
        val spec = VolumeSpecBuilder("config-volume").apply {
            from {
                configMap("app-config")
            }
        }.build()

        JSONAssert.assertEquals(
            """{
              |  "name": "config-volume",
              |  "configMap": {
              |    "name": "app-config"
              |  }
              |}""".trimMargin(),
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    @Test
    fun testSecretSourceContent() {
        val spec = VolumeSpecBuilder("secret-volume").apply {
            from {
                secret("app-secret")
            }
        }.build()

        assertEquals("secret-volume", spec.name)
        val source = assertIs<VolumeSpec.SecretSourceSpec>(spec.source)
        assertEquals("app-secret", source.secretName)
    }

    @Test
    fun testSecretSourceYaml() {
        val spec = VolumeSpecBuilder("secret-volume").apply {
            from {
                secret("app-secret")
            }
        }.build()

        JSONAssert.assertEquals(
            """{
              |  "name": "secret-volume",
              |  "secret": {
              |    "secretName": "app-secret"
              |  }
              |}""".trimMargin(),
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    @Test
    fun testPersistentVolumeClaimSourceContent() {
        val spec = VolumeSpecBuilder("pvc-volume").apply {
            from {
                persistentVolumeClaim("app-pvc")
            }
        }.build()

        assertEquals("pvc-volume", spec.name)
        val source = assertIs<VolumeSpec.PersistentVolumeClaimSourceSpec>(spec.source)
        assertEquals("app-pvc", source.claimName)
    }

    @Test
    fun testPersistentVolumeClaimSourceYaml() {
        val spec = VolumeSpecBuilder("pvc-volume").apply {
            from {
                persistentVolumeClaim("app-pvc")
            }
        }.build()

        JSONAssert.assertEquals(
            """{
              |  "name": "pvc-volume",
              |  "persistentVolumeClaim": {
              |    "claimName": "app-pvc"
              |  }
              |}""".trimMargin(),
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }

    @Test
    fun testHostPathSourceContent() {
        val spec = VolumeSpecBuilder("host-volume").apply {
            from {
                hostPath("/data", VolumeSpec.HostPathSourceSpec.Type.DirectoryOrCreate)
            }
        }.build()

        assertEquals("host-volume", spec.name)
        val source = assertIs<VolumeSpec.HostPathSourceSpec>(spec.source)
        assertEquals("/data", source.path)
        assertEquals(VolumeSpec.HostPathSourceSpec.Type.DirectoryOrCreate, source.type)
    }

    @Test
    fun testHostPathSourceYaml() {
        val spec = VolumeSpecBuilder("host-volume").apply {
            from {
                hostPath("/data", VolumeSpec.HostPathSourceSpec.Type.DirectoryOrCreate)
            }
        }.build()

        JSONAssert.assertEquals(
            """{
              |  "name": "host-volume",
              |  "hostPath": {
              |    "path": "/data",
              |    "type": "DirectoryOrCreate"
              |  }
              |}""".trimMargin(),
            spec.toJson(),
            JSONCompareMode.LENIENT
        )
    }
}