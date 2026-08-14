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
import kotlin.test.assertNull

/**
 * Tests for [VolumeMountSpec] and [VolumeDeviceSpec], covering how a pod volume is exposed inside a
 * container.
 */
class VolumeMountSpecTest {
    companion object {
        private val maxSpec = VolumeMountSpecBuilder("config", "/etc/app").apply {
            readOnly = true
            subPath = "application.yaml"
            mountPropagation = VolumeMountSpec.MountPropagationMode.HostToContainer
            recursiveReadOnly = VolumeMountSpec.RecursiveReadOnlyMode.IfPossible
        }.build()

        private val minSpec = VolumeMountSpecBuilder("data", "/var/data").build()

        private val expressionSpec = VolumeMountSpecBuilder("logs", "/var/log").apply {
            subPathExpr = "\$(POD_NAME)/app.log"
        }.build()

        private val deviceSpec = VolumeDeviceSpecBuilder("block", "/dev/xvda").build()
    }

    /**
     * Verifies that a volume mount with every optional field set is mapped onto the specification.
     *
     * The mount is read-only, exposes only a single file of the volume and pins both the propagation
     * and the recursive read-only behaviour.
     */
    @Test
    fun testMaxContent() {
        assertEquals("config", maxSpec.name)
        assertEquals("/etc/app", maxSpec.mountPath)
        assertEquals(true, maxSpec.readOnly)
        assertEquals("application.yaml", maxSpec.subPath)
        assertNull(maxSpec.subPathExpr)
        assertEquals(VolumeMountSpec.MountPropagationMode.HostToContainer, maxSpec.mountPropagation)
        assertEquals(VolumeMountSpec.RecursiveReadOnlyMode.IfPossible, maxSpec.recursiveReadOnly)
    }

    /**
     * Verifies that a minimal volume mount only carries name and mount path.
     *
     * Everything else must stay unset so Kubernetes applies its own defaults.
     */
    @Test
    fun testMinContent() {
        assertEquals("data", minSpec.name)
        assertEquals("/var/data", minSpec.mountPath)
        assertNull(minSpec.readOnly)
        assertNull(minSpec.subPath)
        assertNull(minSpec.subPathExpr)
        assertNull(minSpec.mountPropagation)
        assertNull(minSpec.recursiveReadOnly)
    }

    /**
     * Verifies that a mount using an environment variable expression carries it instead of a sub-path.
     *
     * This is what allows a per-pod log path to be derived from the downward API.
     */
    @Test
    fun testSubPathExpressionContent() {
        assertEquals("\$(POD_NAME)/app.log", expressionSpec.subPathExpr)
        assertNull(expressionSpec.subPath)
    }

    /**
     * Verifies that a fully configured volume mount is rendered with all of its fields.
     */
    @Test
    fun testMaxYaml() {
        val expectedJson = """
          |{
          |  "name": "config",
          |  "mountPath": "/etc/app",
          |  "readOnly": true,
          |  "subPath": "application.yaml",
          |  "mountPropagation": "HostToContainer",
          |  "recursiveReadOnly": "IfPossible"
          |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, maxSpec.toJson(), JSONCompareMode.STRICT)
    }

    /**
     * Verifies that a minimal volume mount omits every unset field from the rendered manifest.
     */
    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals(
            """{"name":"data","mountPath":"/var/data"}""",
            minSpec.toJson(),
            JSONCompareMode.STRICT
        )
    }

    /**
     * Verifies that a volume device is rendered with its name and device path.
     */
    @Test
    fun testDeviceYaml() {
        JSONAssert.assertEquals(
            """{"name":"block","devicePath":"/dev/xvda"}""",
            deviceSpec.toJson(),
            JSONCompareMode.STRICT
        )
    }

    /**
     * Verifies that a volume mount rejects setting both sub-path forms at once.
     *
     * Kubernetes accepts either the literal sub-path or the expression, never both.
     */
    @Test
    fun testRejectsBothSubPathForms() {
        assertFailsWith<IllegalArgumentException> {
            VolumeMountSpecBuilder("config", "/etc/app").apply {
                subPath = "a"
                subPathExpr = "b"
            }.build()
        }
    }

    /**
     * Verifies that recursive read-only requires the mount to be read-only.
     *
     * The setting only refines a read-only mount, so combining it with a writable mount is invalid.
     */
    @Test
    fun testRecursiveReadOnlyRequiresReadOnly() {
        assertFailsWith<IllegalArgumentException> {
            VolumeMountSpecBuilder("config", "/etc/app").apply {
                recursiveReadOnly = VolumeMountSpec.RecursiveReadOnlyMode.Enabled
            }.build()
        }
    }

    /**
     * Verifies that a mount path containing a colon is rejected.
     *
     * The colon separates fields in the container runtime's mount syntax and would corrupt the mount.
     */
    @Test
    fun testRejectsColonInMountPath() {
        assertFailsWith<IllegalArgumentException> {
            VolumeMountSpecBuilder("config", "/etc:/app").build()
        }
    }
}
