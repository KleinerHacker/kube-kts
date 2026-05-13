/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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

    @Test
    fun testMaxContent() {
        val spec = ContainerSecurityContextSpecBuilder().apply {
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

        assertEquals(1000, spec.runAsUser)
        assertEquals(2000, spec.runAsGroup)
        assertEquals(true, spec.runAsNonRoot)
        assertEquals(true, spec.privileged)
        assertEquals(true, spec.readOnlyRootFilesystem)
        assertEquals(true, spec.allowPrivilegeEscalation)
        assertEquals(ProcMountType.Unmasked, spec.procMount)

        assertNotNull(spec.capabilities)
        assertNotNull(spec.capabilities.add)
        assertEquals("NET_ADMIN", spec.capabilities.add.first())
        assertNotNull(spec.capabilities.drop)
        assertEquals("ALL", spec.capabilities.drop.first())

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
    }

    @Test
    fun testMaxYaml() {
        val spec = ContainerSecurityContextSpecBuilder().apply {
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

        val actualJson = spec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMediumContent() {
        val spec = ContainerSecurityContextSpecBuilder().apply {
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

        assertEquals(1000, spec.runAsUser)
        assertEquals(2000, spec.runAsGroup)
        assertEquals(true, spec.runAsNonRoot)
        assertEquals(true, spec.privileged)
        assertEquals(true, spec.readOnlyRootFilesystem)
        assertEquals(true, spec.allowPrivilegeEscalation)
        assertEquals(ProcMountType.Unmasked, spec.procMount)

        assertNotNull(spec.capabilities)
        assertNull(spec.capabilities.add)
        assertNull(spec.capabilities.drop)

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
    }

    @Test
    fun testMediumYaml() {
        val spec = ContainerSecurityContextSpecBuilder().apply {
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

        val actualJson = spec.toJson()
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

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        val spec = ContainerSecurityContextSpecBuilder().build()

        assertNull(spec.runAsUser)
        assertNull(spec.runAsGroup)
        assertNull(spec.runAsNonRoot)
        assertNull(spec.privileged)
        assertNull(spec.readOnlyRootFilesystem)
        assertNull(spec.allowPrivilegeEscalation)
        assertNull(spec.procMount)
        assertNull(spec.capabilities)
        assertNull(spec.seLinuxOptions)
        assertNull(spec.seccompProfile)
        assertNull(spec.appArmorProfile)
        assertNull(spec.windowsOptions)
    }

    @Test
    fun testMinYaml() {
        val spec = ContainerSecurityContextSpecBuilder().build()

        JSONAssert.assertEquals("""{}""", spec.toJson(), JSONCompareMode.LENIENT)
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