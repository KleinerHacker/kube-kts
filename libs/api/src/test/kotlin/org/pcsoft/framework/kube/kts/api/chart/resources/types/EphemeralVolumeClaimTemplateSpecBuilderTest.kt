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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Tests for [EphemeralVolumeClaimTemplateSpecBuilder], the claim template of an ephemeral volume.
 */
class EphemeralVolumeClaimTemplateSpecBuilderTest {
    /**
     * Verifies that a minimal template only carries the claim specification.
     *
     * Without labels or annotations the metadata block must stay absent from the manifest.
     */
    @Test
    fun testBuildsMinimalTemplate() {
        val spec = EphemeralVolumeClaimTemplateSpecBuilder().apply {
            spec { requests(1.giBytes) }
        }.build()

        assertNull(spec.metadata)
        assertEquals(1.giBytes, spec.spec.resources?.requests?.storage)
    }

    /**
     * Verifies that metadata and the full claim specification are carried into the template.
     */
    @Test
    fun testBuildsMaximalTemplate() {
        val spec = EphemeralVolumeClaimTemplateSpecBuilder().apply {
            metadata(mapOf("app" to "demo"), mapOf("owner" to "team"))
            spec {
                accessModes(VolumeClaimTemplateSpec.AccessMode.ReadWriteOnce)
                storageClassName = "fast"
                volumeMode = VolumeClaimTemplateSpec.VolumeMode.Filesystem
                volumeAttributesClassName = "gold"
                requests(1.giBytes)
                limits(2.giBytes)
            }
        }.build()

        assertEquals(mapOf("app" to "demo"), spec.metadata?.labels)
        assertEquals(mapOf("owner" to "team"), spec.metadata?.annotations)
        assertEquals("fast", spec.spec.storageClassName)
        assertEquals("gold", spec.spec.volumeAttributesClassName)
        assertEquals(2.giBytes, spec.spec.resources?.limits?.storage)
    }

    /**
     * Verifies that a template without a claim specification is rejected.
     *
     * Kubernetes cannot provision the volume without knowing the requested storage.
     */
    @Test
    fun testRejectsMissingSpec() {
        assertFailsWith<IllegalArgumentException> { EphemeralVolumeClaimTemplateSpecBuilder().build() }
    }

    /**
     * Verifies that the ephemeral volume source delegates to the claim template builder.
     *
     * Both the legacy shortcuts and the explicit template block must produce the same result.
     */
    @Test
    fun testEphemeralSourceDelegates() {
        val viaShortcuts = EphemeralSourceSpecBuilder().apply {
            metadata(mapOf("app" to "demo"))
            spec { requests(1.giBytes) }
        }.build()

        val viaTemplate = EphemeralSourceSpecBuilder().apply {
            volumeClaimTemplate {
                metadata(mapOf("app" to "demo"))
                spec { requests(1.giBytes) }
            }
        }.build()

        assertEquals(viaShortcuts.volumeClaimTemplate, viaTemplate.volumeClaimTemplate)
    }
}
