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
    companion object {
        private val maxSpec = PodSecurityContextSpecBuilder().apply {
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

        private val mediumSpec = PodSecurityContextSpecBuilder().apply {
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

        private val minSpec = PodSecurityContextSpecBuilder().build()
    }

    @Test
    fun testMaxContent() {
        assertEquals(1000, maxSpec.runAsUser)
        assertEquals(2000, maxSpec.runAsGroup)
        assertEquals(true, maxSpec.runAsNonRoot)

        assertNotNull(maxSpec.seLinuxOptions)
        assertEquals("s0:c123,c456", maxSpec.seLinuxOptions.level)
        assertEquals("role", maxSpec.seLinuxOptions.role)
        assertEquals("type", maxSpec.seLinuxOptions.type)
        assertEquals("user", maxSpec.seLinuxOptions.user)

        assertNotNull(maxSpec.seccompProfile)
        assertEquals(SecurityContextSpec.ProfileType.Localhost, maxSpec.seccompProfile.type)
        assertNotNull(maxSpec.seccompProfile.localhostProfile)
        assertEquals("localhost", maxSpec.seccompProfile.localhostProfile)

        assertNotNull(maxSpec.appArmorProfile)
        assertEquals(SecurityContextSpec.ProfileType.Localhost, maxSpec.appArmorProfile.type)
        assertNotNull(maxSpec.appArmorProfile.localhostProfile)
        assertEquals("localhost", maxSpec.appArmorProfile.localhostProfile)

        assertNotNull(maxSpec.windowsOptions)
        assertEquals("gmsaCredentialSpecName", maxSpec.windowsOptions.gmsaCredentialSpecName)
        assertEquals("gmsaCredentialSpec", maxSpec.windowsOptions.gmsaCredentialSpec)
        assertEquals("runAsUserName", maxSpec.windowsOptions.runAsUserName)
        assertEquals(true, maxSpec.windowsOptions.hostProcess)

        assertEquals(9999L, maxSpec.fsGroup)
        assertEquals(FSGroupChangePolicy.OnRootMismatch, maxSpec.fsGroupChangePolicy)
        assertEquals(listOf(1000L, 2000L), maxSpec.supplementalGroups)
        assertEquals(SupplementalGroupsPolicy.Merge, maxSpec.supplementalGroupsPolicy)
        assertEquals(mapOf("net.ipv4.ip_forward" to "1"), maxSpec.sysctls)
    }

    @Test
    fun testMaxYaml() {
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

        JSONAssert.assertEquals(expectedJson, maxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testMediumContent() {
        assertEquals(1000, mediumSpec.runAsUser)
        assertEquals(2000, mediumSpec.runAsGroup)
        assertEquals(true, mediumSpec.runAsNonRoot)

        assertNotNull(mediumSpec.seLinuxOptions)
        assertNull(mediumSpec.seLinuxOptions.level)
        assertNull(mediumSpec.seLinuxOptions.role)
        assertNull(mediumSpec.seLinuxOptions.type)
        assertNull(mediumSpec.seLinuxOptions.user)

        assertNotNull(mediumSpec.seccompProfile)
        assertEquals(SecurityContextSpec.ProfileType.RuntimeDefault, mediumSpec.seccompProfile.type)
        assertNull(mediumSpec.seccompProfile.localhostProfile)

        assertNotNull(mediumSpec.appArmorProfile)
        assertEquals(SecurityContextSpec.ProfileType.RuntimeDefault, mediumSpec.appArmorProfile.type)
        assertNull(mediumSpec.appArmorProfile.localhostProfile)

        assertNotNull(mediumSpec.windowsOptions)
        assertNull(mediumSpec.windowsOptions.gmsaCredentialSpecName)
        assertNull(mediumSpec.windowsOptions.gmsaCredentialSpec)
        assertNull(mediumSpec.windowsOptions.runAsUserName)
        assertNull(mediumSpec.windowsOptions.hostProcess)

        assertNull(mediumSpec.fsGroup)
        assertNull(mediumSpec.fsGroupChangePolicy)
        assertNull(mediumSpec.supplementalGroups)
        assertNull(mediumSpec.supplementalGroupsPolicy)
        assertNull(mediumSpec.sysctls)
    }

    @Test
    fun testMediumYaml() {
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

        JSONAssert.assertEquals(expectedJson, mediumSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        assertNull(minSpec.runAsUser)
        assertNull(minSpec.runAsGroup)
        assertNull(minSpec.runAsNonRoot)
        assertNull(minSpec.seLinuxOptions)
        assertNull(minSpec.seccompProfile)
        assertNull(minSpec.appArmorProfile)
        assertNull(minSpec.windowsOptions)
        assertNull(minSpec.fsGroup)
        assertNull(minSpec.fsGroupChangePolicy)
        assertNull(minSpec.supplementalGroups)
        assertNull(minSpec.supplementalGroupsPolicy)
        assertNull(minSpec.sysctls)
    }

    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals("""{}""", minSpec.toJson(), JSONCompareMode.LENIENT)
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
