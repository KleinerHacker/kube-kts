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
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for [ProjectedSourceEntrySpecBuilder] and [ServiceAccountTokenProjectionSpecBuilder].
 */
class ProjectedSourceEntrySpecBuilderTest {
    /**
     * Verifies that a ConfigMap projection carries only the ConfigMap source.
     */
    @Test
    fun testBuildsConfigMapProjection() {
        val spec = ProjectedSourceEntrySpecBuilder().apply {
            configMap { name = "app-config" }
        }.build()

        assertEquals("app-config", spec.configMap?.name)
        assertNull(spec.secret)
        assertNull(spec.downwardAPI)
        assertNull(spec.serviceAccountToken)
    }

    /**
     * Verifies that a ServiceAccount token projection carries audience and expiration.
     */
    @Test
    fun testBuildsServiceAccountTokenProjection() {
        val spec = ProjectedSourceEntrySpecBuilder().apply {
            serviceAccountToken("token") {
                audience = "vault"
                expirationSeconds = 3600L
            }
        }.build()

        assertEquals("token", spec.serviceAccountToken?.path)
        assertEquals("vault", spec.serviceAccountToken?.audience)
        assertEquals(3600L, spec.serviceAccountToken?.expirationSeconds)
    }

    /**
     * Verifies that configuring a second projection replaces the first one.
     *
     * A projected entry may only ever carry a single projection, so the builder must not accumulate.
     */
    @Test
    fun testProjectionsAreMutuallyExclusive() {
        val spec = ProjectedSourceEntrySpecBuilder().apply {
            configMap { name = "app-config" }
            secret { name = "app-secret" }
        }.build()

        assertNull(spec.configMap)
        assertEquals("app-secret", spec.secret?.name)
    }

    /**
     * Verifies that an entry without any projection is rejected.
     */
    @Test
    fun testRejectsEmptyEntry() {
        assertFailsWith<IllegalArgumentException> { ProjectedSourceEntrySpecBuilder().build() }
    }

    /**
     * Verifies that the projected volume DSL routes every shortcut through the entry builder.
     */
    @Test
    fun testSourcesThroughProjectedSource() {
        val spec = ProjectedSourceSpecBuilder().apply {
            sources {
                source { configMap { name = "app-config" } }
            }
            addSecret { name = "app-secret" }
            addDownwardApi { addFieldRef("name", "metadata.name") }
            addServiceAccountToken("token", "vault", 600L)
        }.build()

        assertEquals(4, spec.sources.size)
        assertEquals("app-config", spec.sources[0].configMap?.name)
        assertEquals("app-secret", spec.sources[1].secret?.name)
        assertNotNull(spec.sources[2].downwardAPI)
        assertEquals(600L, spec.sources[3].serviceAccountToken?.expirationSeconds)
    }
}
