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
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodSecurityContextSpec.FSGroupChangePolicy
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodSecurityContextSpec.SupplementalGroupsPolicy
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PodSecurityContextSpecTest {

    @Test
    fun testMaxContent() {
        val spec = PodSecurityContextSpecBuilder().apply {
            runAsUser = 1000
            runAsGroup = 2000
            runAsNonRoot = true
            seLinuxOptions {
                level = "s0:c123,c456"
                role = "role"
                type = "type"
                user = "user"
            }
            seccompProfile(SecurityContextSpec.ProfileType.Localhost) {
                localhostProfile = "localhost"
            }
            appArmorProfile(SecurityContextSpec.ProfileType.Localhost) {
                localhostProfile = "localhost"
            }
            windowsOptions {
                gmsaCredentialSpecName = "gmsaCredentialSpecName"
                gmsaCredentialSpec = "gmsaCredentialSpec"
                runAsUserName = "runAsUserName"
                hostProcess = true
            }
            fsGroup = 9999
            fsGroupChangePolicy = FSGroupChangePolicy.OnRootMismatch
            supplementalGroups {
                group(1000)
                group(2000)
            }
            supplementalGroupsPolicy = SupplementalGroupsPolicy.Merge
            sysctls {
                sysctl("net.ipv4.ip_forward", "1")
            }
        }.build()

        assertEquals(1000, spec.runAsUser)
        assertEquals(2000, spec.runAsGroup)
        assertEquals(true, spec.runAsNonRoot)

        assertNotNull(spec.seLinuxOptions)
        assertEquals("s0:c123,c456", spec.seLinuxOptions.level)
        assertEquals("role", spec.seLinuxOptions.role)
        assertEquals("type", spec.seLinuxOptions.type)
        assertEquals("user", spec.seLinuxOptions.user)

        assertNotNull(spec.seccompProfile)
        assertEquals(SecurityContextSpec.ProfileType.Localhost, spec.seccompProfile.type)
        assertNotNull(spec.seccompProfile.localhostProfile)
        assertEquals("localhost", spec.seccompProfile.localhostProfile)

        assertNotNull(spec.appArmorProfile)
        assertEquals(SecurityContextSpec.ProfileType.Localhost, spec.appArmorProfile.type)
        assertNotNull(spec.appArmorProfile.localhostProfile)
        assertEquals("localhost", spec.appArmorProfile.localhostProfile)

        assertNotNull(spec.windowsOptions)
        assertEquals("gmsaCredentialSpecName", spec.windowsOptions.gmsaCredentialSpecName)
        assertEquals("gmsaCredentialSpec", spec.windowsOptions.gmsaCredentialSpec)
        assertEquals("runAsUserName", spec.windowsOptions.runAsUserName)
        assertEquals(true, spec.windowsOptions.hostProcess)

        assertEquals(9999L, spec.fsGroup)
        assertEquals(FSGroupChangePolicy.OnRootMismatch, spec.fsGroupChangePolicy)
        assertEquals(listOf(1000L, 2000L), spec.supplementalGroups)
        assertEquals(SupplementalGroupsPolicy.Merge, spec.supplementalGroupsPolicy)
        assertEquals(mapOf("net.ipv4.ip_forward" to "1"), spec.sysctls)
    }

    @Test
    fun testMaxYaml() {
        val spec = PodSecurityContextSpecBuilder().apply {
            runAsUser = 1000
            runAsGroup = 2000
            runAsNonRoot = true
            seLinuxOptions {
                level = "s0:c123,c456"
                role = "role"
                type = "type"
                user = "user"
            }
            seccompProfile(SecurityContextSpec.ProfileType.Localhost) {
                localhostProfile = "localhost"
            }
            appArmorProfile(SecurityContextSpec.ProfileType.Localhost) {
                localhostProfile = "localhost"
            }
            windowsOptions {
                gmsaCredentialSpecName = "gmsaCredentialSpecName"
                gmsaCredentialSpec = "gmsaCredentialSpec"
                runAsUserName = "runAsUserName"
                hostProcess = true
            }
            fsGroup = 9999
            fsGroupChangePolicy = FSGroupChangePolicy.OnRootMismatch
            supplementalGroups {
                group(1000)
                group(2000)
            }
            supplementalGroupsPolicy = SupplementalGroupsPolicy.Merge
            sysctls {
                sysctl("net.ipv4.ip_forward", "1")
            }
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{
            |  "runAsUser": 1000,
            |  "runAsGroup": 2000,
            |  "runAsNonRoot": true,
            |  "seLinuxOptions": {
            |    "level": "s0:c123,c456",
            |    "role": "role",
            |    "type": "type",
            |    "user": "user"
            |  },
            |  "seccompProfile": {
            |    "type": "Localhost",
            |    "localhostProfile": "localhost"
            |  },
            |  "appArmorProfile": {
            |    "type": "Localhost",
            |    "localhostProfile": "localhost"
            |  },
            |  "windowsOptions": {
            |    "gmsaCredentialSpecName": "gmsaCredentialSpecName",
            |    "gmsaCredentialSpec": "gmsaCredentialSpec",
            |    "runAsUserName": "runAsUserName",
            |    "hostProcess": true
            |  },
            |  "fsGroup": 9999,
            |  "fsGroupChangePolicy": "OnRootMismatch",
            |  "supplementalGroups": [1000, 2000],
            |  "supplementalGroupsPolicy": "Merge",
            |  "sysctls": [
            |    {
            |      "name": "net.ipv4.ip_forward",
            |      "value": "1"
            |    }
            |  ]
            |}""".trimMargin()

        println(actualJson)
        println(expectedJson.lines().joinToString("") { it.trim().replace(": ", ":") })

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMediumContent() {
        val spec = PodSecurityContextSpecBuilder().apply {
            runAsUser = 1000
            runAsGroup = 2000
            runAsNonRoot = true
            seLinuxOptions {

            }
            seccompProfile(SecurityContextSpec.ProfileType.RuntimeDefault) {

            }
            appArmorProfile(SecurityContextSpec.ProfileType.RuntimeDefault) {

            }
            windowsOptions {

            }
            supplementalGroups {

            }
            sysctls {

            }
        }.build()

        assertEquals(1000, spec.runAsUser)
        assertEquals(2000, spec.runAsGroup)
        assertEquals(true, spec.runAsNonRoot)

        assertNotNull(spec.seLinuxOptions)
        assertNull(spec.seLinuxOptions.level)
        assertNull(spec.seLinuxOptions.role)
        assertNull(spec.seLinuxOptions.type)
        assertNull(spec.seLinuxOptions.user)

        assertNotNull(spec.seccompProfile)
        assertEquals(SecurityContextSpec.ProfileType.RuntimeDefault, spec.seccompProfile.type)
        assertNull(spec.seccompProfile.localhostProfile)

        assertNotNull(spec.appArmorProfile)
        assertEquals(SecurityContextSpec.ProfileType.RuntimeDefault, spec.appArmorProfile.type)
        assertNull(spec.appArmorProfile.localhostProfile)

        assertNotNull(spec.windowsOptions)
        assertNull(spec.windowsOptions.gmsaCredentialSpecName)
        assertNull(spec.windowsOptions.gmsaCredentialSpec)
        assertNull(spec.windowsOptions.runAsUserName)
        assertNull(spec.windowsOptions.hostProcess)

        assertNull(spec.fsGroup)
        assertNull(spec.fsGroupChangePolicy)
        assertNull(spec.supplementalGroups)
        assertNull(spec.supplementalGroupsPolicy)
        assertNull(spec.sysctls)
    }

    @Test
    fun testMediumYaml() {
        val spec = PodSecurityContextSpecBuilder().apply {
            runAsUser = 1000
            runAsGroup = 2000
            runAsNonRoot = true
            seLinuxOptions {

            }
            seccompProfile(SecurityContextSpec.ProfileType.RuntimeDefault) {

            }
            appArmorProfile(SecurityContextSpec.ProfileType.RuntimeDefault) {

            }
            windowsOptions {

            }
            supplementalGroups {

            }
            sysctls {

            }
        }.build()

        val actualJson = spec.toJson()
        val expectedJson = """{
            |  "runAsUser": 1000,
            |  "runAsGroup": 2000,
            |  "runAsNonRoot": true,
            |  "seLinuxOptions": {},
            |  "seccompProfile": {
            |    "type": "RuntimeDefault"
            |  },
            |  "appArmorProfile": {
            |    "type": "RuntimeDefault"
            |  },
            |  "windowsOptions": {}
            |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        val spec = PodSecurityContextSpecBuilder().build()

        assertNull(spec.runAsUser)
        assertNull(spec.runAsGroup)
        assertNull(spec.runAsNonRoot)
        assertNull(spec.seLinuxOptions)
        assertNull(spec.seccompProfile)
        assertNull(spec.appArmorProfile)
        assertNull(spec.windowsOptions)
        assertNull(spec.fsGroup)
        assertNull(spec.fsGroupChangePolicy)
        assertNull(spec.supplementalGroups)
        assertNull(spec.supplementalGroupsPolicy)
        assertNull(spec.sysctls)
    }

    @Test
    fun testMinYaml() {
        val spec = PodSecurityContextSpecBuilder().build()

        JSONAssert.assertEquals("""{}""", spec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testNotAllowedProfile() {
        assertFailsWith<IllegalArgumentException> {
            PodSecurityContextSpecBuilder().apply {
                seccompProfile(SecurityContextSpec.ProfileType.Localhost) {
                }
            }.build()
        }

        assertFailsWith<IllegalArgumentException> {
            PodSecurityContextSpecBuilder().apply {
                appArmorProfile(SecurityContextSpec.ProfileType.Localhost) {
                }
            }.build()
        }
    }

}