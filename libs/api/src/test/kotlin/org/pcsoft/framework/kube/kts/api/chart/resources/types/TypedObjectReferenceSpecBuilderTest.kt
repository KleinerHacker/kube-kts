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
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests for [TypedObjectReferenceSpecBuilder], the builder of a claim data source reference.
 */
class TypedObjectReferenceSpecBuilderTest {
    /**
     * Verifies that a core API reference omits the API group.
     *
     * A PersistentVolumeClaim cloned from another claim lives in the core group, which renders as null.
     */
    @Test
    fun testBuildsCoreReference() {
        val spec = TypedObjectReferenceSpecBuilder("PersistentVolumeClaim", "source-claim").build()

        assertEquals("PersistentVolumeClaim", spec.kind)
        assertEquals("source-claim", spec.name)
        assertNull(spec.apiGroup)
        assertNull(spec.namespace)
    }

    /**
     * Verifies that a cross-namespace reference of a custom resource carries group and namespace.
     */
    @Test
    fun testBuildsCrossNamespaceReference() {
        val spec = TypedObjectReferenceSpecBuilder("VolumeSnapshot", "snapshot").apply {
            apiGroup = "snapshot.storage.k8s.io"
            namespace = "backup"
        }.build()

        assertEquals("snapshot.storage.k8s.io", spec.apiGroup)
        assertEquals("backup", spec.namespace)
    }
}
