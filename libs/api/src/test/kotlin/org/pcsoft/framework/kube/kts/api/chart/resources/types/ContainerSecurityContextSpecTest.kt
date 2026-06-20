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
import org.pcsoft.framework.kube.kts.api.chart.resources.types.ContainerSecurityContextSpec.*
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ContainerSecurityContextSpecTest {
    companion object {
        private val maxSpec = ContainerSecurityContextSpecBuilder().apply {
            runAsUser = 1000
            runAsGroup = 2000
            runAsNonRoot = true
            privileged = true
            readOnlyRootFilesystem = true
            allowPrivilegeEscalation = true
            procMount = ProcMountType.Unmasked
            capabilities {
                add("NET_ADMIN")
                drop("ALL")
            }
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
        }.build()

        private val mediumSpec = ContainerSecurityContextSpecBuilder().apply {
            runAsUser = 1000
            runAsGroup = 2000
            runAsNonRoot = true
            privileged = true
            readOnlyRootFilesystem = true
            allowPrivilegeEscalation = true
            procMount = ProcMountType.Unmasked
            capabilities {

            }
            seLinuxOptions {

            }
            seccompProfile(SecurityContextSpec.ProfileType.RuntimeDefault) {

            }
            appArmorProfile(SecurityContextSpec.ProfileType.RuntimeDefault) {

            }
            windowsOptions {

            }
        }.build()

        private val minSpec = ContainerSecurityContextSpecBuilder().build()
    }

    @Test
    fun testMaxContent() {
        assertEquals(1000, maxSpec.runAsUser)
        assertEquals(2000, maxSpec.runAsGroup)
        assertEquals(true, maxSpec.runAsNonRoot)
        assertEquals(true, maxSpec.privileged)
        assertEquals(true, maxSpec.readOnlyRootFilesystem)
        assertEquals(true, maxSpec.allowPrivilegeEscalation)
        assertEquals(ProcMountType.Unmasked, maxSpec.procMount)

        assertNotNull(maxSpec.capabilities)
        assertNotNull(maxSpec.capabilities.add)
        assertEquals("NET_ADMIN", maxSpec.capabilities.add.first())
        assertNotNull(maxSpec.capabilities.drop)
        assertEquals("ALL", maxSpec.capabilities.drop.first())

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
    }

    @Test
    fun testMaxYaml() {
        val expectedJson = """{
            |  "runAsUser": 1000,
            |  "runAsGroup": 2000,
            |  "runAsNonRoot": true,
            |  "privileged": true,
            |  "readOnlyRootFilesystem": true,
            |  "allowPrivilegeEscalation": true,
            |  "procMount": "Unmasked",
            |  "capabilities": {
            |    "add": [
            |      "NET_ADMIN"
            |    ],
            |    "drop": [
            |      "ALL"
            |    ]
            |  },
            |  "seLinuxOptions": {
            |    "level": "s0:c123,c456",
            |    "role": "role",
            |    "type": "type",
            |    "user": "user"
            |  },
            |  "seccompProfile": {
            |    "localhostProfile": "localhost"
            |  },
            |  "appArmorProfile": {
            |    "localhostProfile": "localhost"
            |  },
            |  "windowsOptions": {
            |    "gmsaCredentialSpecName": "gmsaCredentialSpecName",
            |    "gmsaCredentialSpec": "gmsaCredentialSpec",
            |    "runAsUserName": "runAsUserName",
            |    "hostProcess": true
            |  }
            |}""".trimMargin()

        JSONAssert.assertEquals(expectedJson, maxSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testMediumContent() {
        assertEquals(1000, mediumSpec.runAsUser)
        assertEquals(2000, mediumSpec.runAsGroup)
        assertEquals(true, mediumSpec.runAsNonRoot)
        assertEquals(true, mediumSpec.privileged)
        assertEquals(true, mediumSpec.readOnlyRootFilesystem)
        assertEquals(true, mediumSpec.allowPrivilegeEscalation)
        assertEquals(ProcMountType.Unmasked, mediumSpec.procMount)

        assertNotNull(mediumSpec.capabilities)
        assertNull(mediumSpec.capabilities.add)
        assertNull(mediumSpec.capabilities.drop)

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
    }

    @Test
    fun testMediumYaml() {
        val expectedJson = """{
            |  "runAsUser": 1000,
            |  "runAsGroup": 2000,
            |  "runAsNonRoot": true,
            |  "privileged": true,
            |  "readOnlyRootFilesystem": true,
            |  "allowPrivilegeEscalation": true,
            |  "procMount": "Unmasked",
            |  "capabilities": {},
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
        assertNull(minSpec.privileged)
        assertNull(minSpec.readOnlyRootFilesystem)
        assertNull(minSpec.allowPrivilegeEscalation)
        assertNull(minSpec.procMount)
        assertNull(minSpec.capabilities)
        assertNull(minSpec.seLinuxOptions)
        assertNull(minSpec.seccompProfile)
        assertNull(minSpec.appArmorProfile)
        assertNull(minSpec.windowsOptions)
    }

    @Test
    fun testMinYaml() {
        JSONAssert.assertEquals("""{}""", minSpec.toJson(), JSONCompareMode.LENIENT)
    }

    @Test
    fun testNotAllowedProfile() {
        assertFailsWith<IllegalArgumentException> {
            ContainerSecurityContextSpecBuilder().apply {
                seccompProfile(SecurityContextSpec.ProfileType.Localhost) {
                }
            }.build()
        }

        assertFailsWith<IllegalArgumentException> {
            ContainerSecurityContextSpecBuilder().apply {
                appArmorProfile(SecurityContextSpec.ProfileType.Localhost) {
                }
            }.build()
        }
    }

}
