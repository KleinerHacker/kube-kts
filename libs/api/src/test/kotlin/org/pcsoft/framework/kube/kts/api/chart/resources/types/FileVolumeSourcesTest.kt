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
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for the file-based volume sources that expose data as files: the downward API and projected
 * volumes.
 *
 * The ConfigMap and Secret sources, which share the same base class, are covered by [VolumeSpecTest].
 */
class FileVolumeSourcesTest {
    companion object {
        private val downwardApiMaxSpec = VolumeSpecBuilder("podinfo").apply {
            from {
                downwardApi {
                    defaultMode = 420
                    addFieldRef("labels", "metadata.labels", apiVersion = "v1", mode = 384)
                    addResourceFieldRef(
                        "cpu_limit",
                        "limits.cpu",
                        containerName = "app",
                        divisor = "1m",
                        mode = 384
                    )
                }
            }
        }.build()

        private val downwardApiMinSpec = VolumeSpecBuilder("podinfo").apply {
            from {
                downwardApi {
                    addFieldRef("name", "metadata.name")
                }
            }
        }.build()

        private val projectedMaxSpec = VolumeSpecBuilder("bundle").apply {
            from {
                projected {
                    defaultMode = 420
                    addConfigMap {
                        name = "app-config"
                        optional = true
                    }
                    addSecret {
                        name = "app-secret"
                    }
                    addDownwardApi {
                        addFieldRef("namespace", "metadata.namespace")
                    }
                    addServiceAccountToken("token", audience = "vault", expirationSeconds = 3600)
                }
            }
        }.build()

        private val projectedMinSpec = VolumeSpecBuilder("bundle").apply {
            from {
                projected {
                    addConfigMap { name = "app-config" }
                }
            }
        }.build()
    }

    /**
     * Verifies that a downward API volume with every optional field set is mapped onto the
     * specification.
     *
     * Both selector flavours are used, so the builder must produce one item carrying a `fieldRef` and
     * one carrying a `resourceFieldRef`, each with its own file mode.
     */
    @Test
    fun testDownwardApiMaxContent() {
        val source = assertIs<DownwardApiSourceSpec>(downwardApiMaxSpec.source)
        assertEquals(420, source.defaultMode)
        assertEquals(2, source.items.size)

        val field = source.items[0]
        assertEquals("labels", field.path)
        assertEquals(384, field.mode)
        assertNotNull(field.fieldRef)
        assertEquals("metadata.labels", field.fieldRef.fieldPath)
        assertEquals("v1", field.fieldRef.apiVersion)
        assertNull(field.resourceFieldRef)

        val resource = source.items[1]
        assertEquals("cpu_limit", resource.path)
        assertNotNull(resource.resourceFieldRef)
        assertEquals("limits.cpu", resource.resourceFieldRef.resource)
        assertEquals("app", resource.resourceFieldRef.containerName)
        assertEquals("1m", resource.resourceFieldRef.divisor)
        assertNull(resource.fieldRef)
    }

    /**
     * Verifies that a minimal downward API volume only carries the single required item.
     *
     * No optional value is set, so the default mode and every optional field of the item must stay null.
     */
    @Test
    fun testDownwardApiMinContent() {
        val source = assertIs<DownwardApiSourceSpec>(downwardApiMinSpec.source)
        assertNull(source.defaultMode)
        assertEquals(1, source.items.size)
        assertEquals("name", source.items[0].path)
        assertNull(source.items[0].mode)
        val fieldRef = source.items[0].fieldRef
        assertNotNull(fieldRef)
        assertNull(fieldRef.apiVersion)
    }

    /**
     * Verifies that a downward API volume is rendered under the `downwardAPI` key with its items.
     *
     * The source must appear as a sibling of `name`, matching the flattened shape Kubernetes expects.
     */
    @Test
    fun testDownwardApiMaxYaml() {
        val expectedJson = """
          |{
          |  "name": "podinfo",
          |  "downwardAPI": {
          |    "defaultMode": 420,
          |    "items": [
          |      {
          |        "path": "labels",
          |        "mode": 384,
          |        "fieldRef": {
          |          "fieldPath": "metadata.labels",
          |          "apiVersion": "v1"
          |        }
          |      },
          |      {
          |        "path": "cpu_limit",
          |        "mode": 384,
          |        "resourceFieldRef": {
          |          "resource": "limits.cpu",
          |          "containerName": "app",
          |          "divisor": "1m"
          |        }
          |      }
          |    ]
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, downwardApiMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that a projected volume merges all four projection kinds into one source.
     *
     * Each entry must carry exactly one populated projection, in the order they were added.
     */
    @Test
    fun testProjectedMaxContent() {
        val source = assertIs<ProjectedSourceSpec>(projectedMaxSpec.source)
        assertEquals(420, source.defaultMode)
        assertEquals(4, source.sources.size)

        val configMap = source.sources[0].configMap
        assertNotNull(configMap)
        assertEquals("app-config", configMap.name)
        assertEquals(true, configMap.optional)

        val secret = source.sources[1].secret
        assertNotNull(secret)
        assertEquals("app-secret", secret.name)

        val downwardApi = source.sources[2].downwardAPI
        assertNotNull(downwardApi)
        assertEquals("namespace", downwardApi.items[0].path)

        val token = source.sources[3].serviceAccountToken
        assertNotNull(token)
        assertEquals("token", token.path)
        assertEquals("vault", token.audience)
        assertEquals(3600L, token.expirationSeconds)
    }

    /**
     * Verifies that a minimal projected volume carries a single projection and no default mode.
     */
    @Test
    fun testProjectedMinContent() {
        val source = assertIs<ProjectedSourceSpec>(projectedMinSpec.source)
        assertNull(source.defaultMode)
        assertEquals(1, source.sources.size)
        assertNotNull(source.sources[0].configMap)
        assertNull(source.sources[0].secret)
    }

    /**
     * Verifies that a projected volume is rendered under the `projected` key with its `sources` list.
     */
    @Test
    fun testProjectedMaxYaml() {
        val expectedJson = """
          |{
          |  "name": "bundle",
          |  "projected": {
          |    "defaultMode": 420,
          |    "sources": [
          |      { "configMap": { "name": "app-config", "optional": true } },
          |      { "secret": { "secretName": "app-secret" } },
          |      { "downwardAPI": { "items": [ { "path": "namespace" } ] } },
          |      { "serviceAccountToken": {
          |          "path": "token",
          |          "audience": "vault",
          |          "expirationSeconds": 3600
          |      } }
          |    ]
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, projectedMaxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that a downward API item rejects having both selector kinds set at once.
     *
     * Kubernetes allows exactly one of `fieldRef` and `resourceFieldRef` per item, so building an item
     * with both must fail rather than produce a manifest the API server rejects.
     */
    @Test
    fun testDownwardApiItemRejectsBothSelectors() {
        assertFailsWith<IllegalArgumentException> {
            DownwardApiItemSpec(
                path = "invalid",
                fieldRef = ObjectFieldSelectorSpec("metadata.name", null),
                resourceFieldRef = ResourceFieldSelectorSpec("limits.cpu", "app", null),
                mode = null
            )
        }
    }

    /**
     * Verifies that a projected entry rejects carrying more than one projection.
     *
     * Each entry of a projected volume addresses exactly one source, so a combination must be refused.
     */
    @Test
    fun testProjectedEntryRejectsMultipleProjections() {
        assertFailsWith<IllegalArgumentException> {
            ProjectedSourceEntrySpec(
                configMap = ConfigMapSourceSpec("config", null, null, null),
                secret = SecretSourceSpec("secret", null, null, null),
                downwardAPI = null,
                serviceAccountToken = null
            )
        }
    }

    /**
     * Verifies that a key mapping rejects an absolute path or a path escaping the volume root.
     *
     * Kubernetes requires projected paths to stay inside the volume, so both forms must be refused.
     */
    @Test
    fun testKeyToPathRejectsUnsafePaths() {
        assertFailsWith<IllegalArgumentException> { KeyToPathSpec("key", "/absolute", null) }
        assertFailsWith<IllegalArgumentException> { KeyToPathSpec("key", "nested/../escape", null) }
    }
}
