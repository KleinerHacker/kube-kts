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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.SecurityContextSpec.*

class SecurityContextSpecBuilder internal constructor() {
    private var capabilities: CapabilitiesSpecBuilder? = null
    private var seLinuxOptions: SELinuxOptionsSpecBuilder? = null
    private var seccompProfile: ProfileSpecBuilder? = null
    private var appArmorProfile: ProfileSpecBuilder? = null
    private var windowsOptions: WindowsOptionsSpecBuilder? = null

    var procMount: ProcMountType? = null
    var runAsUser: Long? = null
    var runAsGroup: Long? = null
    var runAsNonRoot: Boolean? = null
    var privileged: Boolean? = null
    var readOnlyRootFilesystem: Boolean? = null
    var allowPrivilegeEscalation: Boolean? = null

    fun addCapability(capability: String) {
        if (capabilities == null) {
            capabilities = CapabilitiesSpecBuilder()
        }
        capabilities!!.add(capability)
    }

    fun addCapabilities(vararg capabilities: String) {
        if (this.capabilities == null) {
            this.capabilities = CapabilitiesSpecBuilder()
        }
        this.capabilities!!.addAll(*capabilities)
    }

    fun dropCapability(capability: String) {
        if (capabilities == null) {
            capabilities = CapabilitiesSpecBuilder()
        }
        capabilities!!.drop(capability)
    }

    fun dropCapabilities(vararg capabilities: String) {
        if (this.capabilities == null) {
            this.capabilities = CapabilitiesSpecBuilder()
        }
        this.capabilities!!.dropAll(*capabilities)
    }

    fun capabilities(prepare: CapabilitiesSpecBuilder.() -> Unit) {
        capabilities = CapabilitiesSpecBuilder().apply(prepare)
    }

    fun seLinuxOptions(prepare: SELinuxOptionsSpecBuilder.() -> Unit) {
        seLinuxOptions = SELinuxOptionsSpecBuilder().apply(prepare)
    }

    fun seccompProfile(type: ProfileType, prepare: ProfileSpecBuilder.() -> Unit = {}) {
        seccompProfile = ProfileSpecBuilder(type).apply(prepare)
    }

    fun appArmorProfile(type: ProfileType, prepare: ProfileSpecBuilder.() -> Unit = {}) {
        appArmorProfile = ProfileSpecBuilder(type).apply(prepare)
    }

    fun windowsOptions(prepare: WindowsOptionsSpecBuilder.() -> Unit) {
        windowsOptions = WindowsOptionsSpecBuilder().apply(prepare)
    }

    internal fun build(): SecurityContextSpec = SecurityContextSpec(
        runAsUser,
        runAsGroup,
        runAsNonRoot,
        privileged,
        readOnlyRootFilesystem,
        allowPrivilegeEscalation,
        procMount,
        capabilities?.build(),
        seLinuxOptions?.build(),
        seccompProfile?.build(),
        appArmorProfile?.build(),
        windowsOptions?.build()
    )

    class CapabilitiesSpecBuilder internal constructor() {
        private var add: MutableList<String>? = null
        private var drop: MutableList<String>? = null

        fun add(capability: String) {
            if (add == null) {
                add = mutableListOf()
            }
            add!!.add(capability)
        }

        fun addAll(vararg capabilities: String) = capabilities.forEach { add(it) }

        fun drop(capability: String) {
            if (drop == null) {
                drop = mutableListOf()
            }
            drop!!.add(capability)
        }

        fun dropAll(vararg capabilities: String) = capabilities.forEach { drop(it) }

        internal fun build(): CapabilitiesSpec = CapabilitiesSpec(add, drop)
    }

    class SELinuxOptionsSpecBuilder internal constructor() {
        var user: String? = null
        var role: String? = null
        var type: String? = null
        var level: String? = null

        internal fun build(): SELinuxOptionsSpec =
            SELinuxOptionsSpec(user, role, type, level)
    }

    class WindowsOptionsSpecBuilder internal constructor() {
        var gmsaCredentialSpecName: String? = null
        var gmsaCredentialSpec: String? = null
        var runAsUserName: String? = null
        var hostProcess: Boolean? = null

        internal fun build(): WindowsOptionsSpec =
            WindowsOptionsSpec(gmsaCredentialSpecName, gmsaCredentialSpec, runAsUserName, hostProcess)
    }

    class ProfileSpecBuilder internal constructor(private val type: ProfileType) {
        var localhostProfile: String? = null

        internal fun build(): ProfileSpec =
            ProfileSpec(type, localhostProfile)
    }
}