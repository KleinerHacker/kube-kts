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
import org.pcsoft.framework.kube.kts.api.types.giBytes
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for [VolumeClaimTemplateSpec], the template a StatefulSet provisions one PersistentVolumeClaim
 * per replica from.
 */
class VolumeClaimTemplateSpecTest {
    companion object {
        private val maxSpec = VolumeClaimTemplateSpecBuilder("data").apply {
            label("tier", "storage")
            annotation("owner", "team")
            accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
            storageClassName = "fast"
            volumeMode = VolumeClaimTemplateSpec.VolumeMode.Filesystem
            volumeName = "pv-manual"
            volumeAttributesClassName = "gold"
            selector {
                addMatchLabel("tier", "fast")
            }
            dataSource("VolumeSnapshot", "snapshot-1", "snapshot.storage.k8s.io")
            dataSourceRef("VolumeSnapshot", "snapshot-2", "snapshot.storage.k8s.io", "other-namespace")
            requests {
                storage = 10.giBytes
            }
            limits {
                storage = 20.giBytes
            }
        }.build()

        private val minSpec = VolumeClaimTemplateSpecBuilder("data").apply {
            requests {
                storage = 1.giBytes
            }
        }.build()
    }

    /**
     * Verifies that a claim template with every optional field set is mapped onto the specification.
     *
     * This covers the fields added for static binding and volume population: the label selector, the
     * explicit volume name, both data source forms and the attributes class.
     */
    @Test
    fun testMaxContent() {
        assertEquals("data", maxSpec.metadata.name)
        assertEquals(mapOf("tier" to "storage"), maxSpec.metadata.labels)
        assertEquals(mapOf("owner" to "team"), maxSpec.metadata.annotations)

        val spec = maxSpec.spec
        assertEquals(listOf(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce), spec.accessModes)
        assertEquals("fast", spec.storageClassName)
        assertEquals(VolumeClaimTemplateSpec.VolumeMode.Filesystem, spec.volumeMode)
        assertEquals("pv-manual", spec.volumeName)
        assertEquals("gold", spec.volumeAttributesClassName)

        val selector = spec.selector
        assertNotNull(selector)
        assertEquals(mapOf("tier" to "fast"), selector.matchLabels)

        val dataSource = spec.dataSource
        assertNotNull(dataSource)
        assertEquals("VolumeSnapshot", dataSource.kind)
        assertEquals("snapshot-1", dataSource.name)
        assertEquals("snapshot.storage.k8s.io", dataSource.apiGroup)
        assertNull(dataSource.namespace)

        val dataSourceRef = spec.dataSourceRef
        assertNotNull(dataSourceRef)
        assertEquals("snapshot-2", dataSourceRef.name)
        assertEquals("other-namespace", dataSourceRef.namespace)

        assertEquals(10.giBytes, spec.resources?.requests?.storage)
        assertEquals(20.giBytes, spec.resources?.limits?.storage)
    }

    /**
     * Verifies that a minimal claim template only carries its name and the requested storage.
     *
     * Everything else must stay unset so the cluster's default storage class and access mode apply.
     */
    @Test
    fun testMinContent() {
        assertEquals("data", minSpec.metadata.name)
        assertNull(minSpec.metadata.labels)
        assertNull(minSpec.spec.storageClassName)
        assertNull(minSpec.spec.selector)
        assertNull(minSpec.spec.volumeName)
        assertNull(minSpec.spec.dataSource)
        assertNull(minSpec.spec.dataSourceRef)
        assertNull(minSpec.spec.volumeAttributesClassName)
        assertEquals(1.giBytes, minSpec.spec.resources?.requests?.storage)
    }

    /**
     * Verifies that a fully configured claim template is rendered with all of its fields.
     */
    @Test
    fun testMaxYaml() {
        val expectedJson = """
          |{
          |  "metadata": {
          |    "name": "data",
          |    "labels": { "tier": "storage" },
          |    "annotations": { "owner": "team" }
          |  },
          |  "spec": {
          |    "accessModes": [ "ReadWriteOnce" ],
          |    "storageClassName": "fast",
          |    "volumeMode": "Filesystem",
          |    "volumeName": "pv-manual",
          |    "volumeAttributesClassName": "gold",
          |    "selector": { "matchLabels": { "tier": "fast" } },
          |    "dataSource": {
          |      "kind": "VolumeSnapshot",
          |      "name": "snapshot-1",
          |      "apiGroup": "snapshot.storage.k8s.io"
          |    },
          |    "dataSourceRef": {
          |      "kind": "VolumeSnapshot",
          |      "name": "snapshot-2",
          |      "apiGroup": "snapshot.storage.k8s.io",
          |      "namespace": "other-namespace"
          |    },
          |    "resources": {
          |      "requests": { "storage": "10Gi" },
          |      "limits": { "storage": "20Gi" }
          |    }
          |  }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, maxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    /**
     * Verifies that a minimal claim template omits every unset field from the rendered manifest.
     */
    @Test
    fun testMinYaml() {
        val expectedJson = """
          |{
          |  "metadata": { "name": "data" },
          |  "spec": { "resources": { "requests": { "storage": "1Gi" } } }
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, minSpec.toJson(), JSONCompareMode.STRICT)
    }

    /**
     * Verifies that a data source reference rejects a blank kind or name.
     *
     * Both identify the object the volume is populated from, so neither may be empty.
     */
    @Test
    fun testDataSourceRejectsBlankValues() {
        assertFailsWith<IllegalArgumentException> { TypedObjectReferenceSpec("", "name", null, null) }
        assertFailsWith<IllegalArgumentException> { TypedObjectReferenceSpec("Kind", " ", null, null) }
    }

    /**
     * Verifies that a claim template rejects a blank name.
     *
     * The name is what a container's volume mount refers to, so it is mandatory.
     */
    @Test
    fun testRejectsBlankName() {
        assertFailsWith<IllegalArgumentException> {
            VolumeClaimTemplateSpec.Metadata("", null, null)
        }
    }
}
