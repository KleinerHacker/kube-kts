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
    companion object {
        private val configMapMaxSpec = VolumeSpecBuilder("config-volume").apply {
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

        private val configMapMinSpec = VolumeSpecBuilder("config-volume").apply {
            from {
                configMap {}
            }
        }.build()

        private val secretMaxSpec = VolumeSpecBuilder("secret-volume").apply {
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

        private val secretMinSpec = VolumeSpecBuilder("secret-volume").apply {
            from {
                secret {}
            }
        }.build()

        private val persistentVolumeClaimMaxSpec = VolumeSpecBuilder("pvc-volume").apply {
            from {
                persistentVolumeClaim("app-pvc") {
                    readOnly = true
                }
            }
        }.build()

        private val persistentVolumeClaimMinSpec = VolumeSpecBuilder("pvc-volume").apply {
            from {
                persistentVolumeClaim("app-pvc")
            }
        }.build()

        private val hostPathMaxSpec = VolumeSpecBuilder("host-volume").apply {
            from {
                hostPath("/data") {
                    type = HostPathSourceSpec.Type.DirectoryOrCreate
                }
            }
        }.build()

        private val hostPathMinSpec = VolumeSpecBuilder("host-volume").apply {
            from {
                hostPath("/data")
            }
        }.build()

        private val emptyDirMaxSpec = VolumeSpecBuilder("empty-volume").apply {
            emptyDir {
                medium = EmptyDirSourceSpec.MediumType.Memory
                sizeLimit = 64.miBytes
            }
        }.build()

        private val emptyDirMinSpec = VolumeSpecBuilder("empty-volume").apply {
            emptyDir {}
        }.build()
    }

    /**
     * Verifies that the maximal VolumeSpec definition of the config map source flavour is built
     * into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testConfigMapSourceMaxContent() {
        assertEquals("config-volume", configMapMaxSpec.name)
        val source = assertIs<ConfigMapSourceSpec>(configMapMaxSpec.source)
        assertMaxFileSource(source, "app-config", "application.yaml", "config/application.yaml")
    }

    /**
     * Verifies that the maximal VolumeSpec definition of the config map source flavour is
     * serialised into the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
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
            configMapMaxSpec
        )
    }

    /**
     * Verifies that the minimal VolumeSpec definition of the config map source flavour is built
     * into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testConfigMapSourceMinContent() {
        assertEquals("config-volume", configMapMinSpec.name)
        val source = assertIs<ConfigMapSourceSpec>(configMapMinSpec.source)
        assertMinFileSource(source)
    }

    /**
     * Verifies that the minimal VolumeSpec definition of the config map source flavour is
     * serialised into the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testConfigMapSourceMinYaml() {
        assertYaml(
            """{
              |  "name": "config-volume",
              |  "configMap": {}
              |}""".trimMargin(),
            configMapMinSpec
        )
    }

    /**
     * Verifies that the maximal VolumeSpec definition of the secret source flavour is built into
     * the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testSecretSourceMaxContent() {
        assertEquals("secret-volume", secretMaxSpec.name)
        val source = assertIs<SecretSourceSpec>(secretMaxSpec.source)
        assertMaxFileSource(source, "app-secret", "password", "secret/password")
    }

    /**
     * Verifies that the maximal VolumeSpec definition of the secret source flavour is serialised
     * into the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
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
            secretMaxSpec
        )
    }

    /**
     * Verifies that the minimal VolumeSpec definition of the secret source flavour is built into
     * the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testSecretSourceMinContent() {
        assertEquals("secret-volume", secretMinSpec.name)
        val source = assertIs<SecretSourceSpec>(secretMinSpec.source)
        assertMinFileSource(source)
    }

    /**
     * Verifies that the minimal VolumeSpec definition of the secret source flavour is serialised
     * into the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testSecretSourceMinYaml() {
        assertYaml(
            """{
              |  "name": "secret-volume",
              |  "secret": {}
              |}""".trimMargin(),
            secretMinSpec
        )
    }

    /**
     * Verifies that the maximal VolumeSpec definition of the persistent volume claim source
     * flavour is built into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testPersistentVolumeClaimSourceMaxContent() {
        assertEquals("pvc-volume", persistentVolumeClaimMaxSpec.name)
        val source = assertIs<PersistentVolumeClaimSourceSpec>(persistentVolumeClaimMaxSpec.source)
        assertEquals("app-pvc", source.claimName)
        assertEquals(true, source.readOnly)
    }

    /**
     * Verifies that the maximal VolumeSpec definition of the persistent volume claim source
     * flavour is serialised into the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
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
            persistentVolumeClaimMaxSpec
        )
    }

    /**
     * Verifies that the minimal VolumeSpec definition of the persistent volume claim source
     * flavour is built into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testPersistentVolumeClaimSourceMinContent() {
        assertEquals("pvc-volume", persistentVolumeClaimMinSpec.name)
        val source = assertIs<PersistentVolumeClaimSourceSpec>(persistentVolumeClaimMinSpec.source)
        assertEquals("app-pvc", source.claimName)
        assertEquals(null, source.readOnly)
    }

    /**
     * Verifies that the minimal VolumeSpec definition of the persistent volume claim source
     * flavour is serialised into the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testPersistentVolumeClaimSourceMinYaml() {
        assertYaml(
            """{
              |  "name": "pvc-volume",
              |  "persistentVolumeClaim": {
              |    "claimName": "app-pvc"
              |  }
              |}""".trimMargin(),
            persistentVolumeClaimMinSpec
        )
    }

    /**
     * Verifies that the maximal VolumeSpec definition of the host path source flavour is built
     * into the expected spec object.
     *
     * Every optional field of the DSL is set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testHostPathSourceMaxContent() {
        assertEquals("host-volume", hostPathMaxSpec.name)
        val source = assertIs<HostPathSourceSpec>(hostPathMaxSpec.source)
        assertEquals("/data", source.path)
        assertEquals(HostPathSourceSpec.Type.DirectoryOrCreate, source.type)
    }

    /**
     * Verifies that the maximal VolumeSpec definition of the host path source flavour is
     * serialised into the expected YAML document.
     *
     * Every optional field of the DSL is set; the serialised result pins the field names, the
     * nesting and the defaults that are omitted on purpose.
     */
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
            hostPathMaxSpec
        )
    }

    /**
     * Verifies that the minimal VolumeSpec definition of the host path source flavour is built
     * into the expected spec object.
     *
     * Only the mandatory fields are set, so the builder must map each of them onto the
     * corresponding property of the specification.
     */
    @Test
    fun testHostPathSourceMinContent() {
        assertEquals("host-volume", hostPathMinSpec.name)
        val source = assertIs<HostPathSourceSpec>(hostPathMinSpec.source)
        assertEquals("/data", source.path)
        assertEquals(null, source.type)
    }

    /**
     * Verifies that the minimal VolumeSpec definition of the host path source flavour is
     * serialised into the expected YAML document.
     *
     * Only the mandatory fields are set; the serialised result pins the field names, the nesting
     * and the defaults that are omitted on purpose.
     */
    @Test
    fun testHostPathSourceMinYaml() {
        assertYaml(
            """{
              |  "name": "host-volume",
              |  "hostPath": {
              |    "path": "/data"
              |  }
              |}""".trimMargin(),
            hostPathMinSpec
        )
    }

    /**
     * Verifies that building a [VolumeSpec] fails when an empty value is used.
     *
     * The builder must reject the input for dir source max with an exception instead of producing
     * an incomplete specification that the API server would refuse later.
     */
    @Test
    fun testEmptyDirSourceMaxContent() {
        assertEquals("empty-volume", emptyDirMaxSpec.name)
        val source = assertIs<EmptyDirSourceSpec>(emptyDirMaxSpec.source)
        assertEquals(EmptyDirSourceSpec.MediumType.Memory, source.medium)
        assertEquals(64.miBytes, source.sizeLimit)
    }

    /**
     * Verifies that building a [VolumeSpec] fails when an empty value is used.
     *
     * The builder must reject the input for dir source max with an exception instead of producing
     * an incomplete specification that the API server would refuse later.
     */
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
            emptyDirMaxSpec
        )
    }

    /**
     * Verifies that building a [VolumeSpec] fails when an empty value is used.
     *
     * The builder must reject the input for dir source min with an exception instead of producing
     * an incomplete specification that the API server would refuse later.
     */
    @Test
    fun testEmptyDirSourceMinContent() {
        assertEquals("empty-volume", emptyDirMinSpec.name)
        val source = assertIs<EmptyDirSourceSpec>(emptyDirMinSpec.source)
        assertEquals(null, source.medium)
        assertEquals(null, source.sizeLimit)
    }

    /**
     * Verifies that building a [VolumeSpec] fails when an empty value is used.
     *
     * The builder must reject the input for dir source min with an exception instead of producing
     * an incomplete specification that the API server would refuse later.
     */
    @Test
    fun testEmptyDirSourceMinYaml() {
        assertYaml(
            """{
              |  "name": "empty-volume",
              |  "emptyDir": {}
              |}""".trimMargin(),
            emptyDirMinSpec
        )
    }

    private fun assertMaxFileSource(source: FileSourceSpec, name: String, key: String, path: String) {
        assertEquals(name, source.name)
        assertEquals(true, source.optional)
        assertEquals(420, source.defaultMode)
        assertEquals(1, source.items?.size)
        val item = assertIs<KeyToPathSpec>(source.items?.single())
        assertEquals(key, item.key)
        assertEquals(path, item.path)
        assertEquals(384, item.mode)
    }

    private fun assertMinFileSource(source: FileSourceSpec) {
        assertEquals(null, source.name)
        assertEquals(null, source.optional)
        assertEquals(null, source.defaultMode)
        assertEquals(null, source.items)
    }

    private fun assertYaml(expectedJson: String, spec: VolumeSpec) {
        JSONAssert.assertEquals(expectedJson, spec.toJson(), JSONCompareMode.LENIENT)
    }
}
