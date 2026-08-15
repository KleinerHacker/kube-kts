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

/**
 * Builder class for constructing a `ContainerSecurityContextSpec` object which defines 
 * the security-related configurations for a container.
 *
 * This class allows setting specific properties related to the container's security context, 
 * such as process namespace configuration (`procMount`) and defining capabilities to be added 
 * or dropped. It extends from `SecurityContextSpecBuilder` to inherit common security context 
 * configuration options.
 *
 * This builder is not intended for direct instantiation but is created and utilized 
 * through internal mechanisms.
 */
class ContainerSecurityContextSpecBuilder internal constructor() :
    SecurityContextSpecBuilder<ContainerSecurityContextSpec>() {
    private var capabilities: CapabilitiesSpecBuilder? = null

    /**
     * Configures the mount type for the /proc filesystem within the container.
     *
     * This property determines how the /proc filesystem is exposed to the container,
     * influencing the level of security and visibility of process-related information.
     *
     * By default, it is null, indicating the absence of an explicit configuration.
     * When set, it uses the specified value from the `ProcMountType` enum.
     */
    var procMount: ContainerSecurityContextSpec.ProcMountType? = null

    /**
     * Specifies whether the container should be allowed to run with elevated privileges.
     *
     * When set to true, the container is granted additional administrative permissions,
     * bypassing certain security restrictions in the runtime environment.
     * This can enable access to sensitive resources or operations typically restricted
     * in a standard container environment.
     *
     * When set to false or null, elevated privileges are not allowed, and the container
     * operates within the default restricted security configuration.
     *
     * This property is commonly used in scenarios where privileged operations are required,
     * such as debugging, custom network configuration, or managing system-level resources.
     * Exercise caution when enabling this option, as it can increase security risks.
     */
    var privileged: Boolean? = null

    /**
     * Indicates whether the root filesystem should be mounted as read-only.
     *
     * When this property is set to true, the container's root filesystem will be mounted
     * as read-only, preventing any modifications to the file system during runtime. This can
     * be used to enhance security by ensuring that containerized applications cannot
     * inadvertently or maliciously modify files or configurations in their root filesystem.
     *
     * If this property is null, the default behavior of mounting the filesystem read/write
     * may be applied, depending on the container runtime and specific configurations.
     */
    var readOnlyRootFilesystem: Boolean? = null

    /**
     * Specifies whether a process can gain more privileges than it started with.
     *
     * When set to `true`, the container is allowed to enable privilege escalation,
     * such as through the use of the `setuid` or `setgid` bit in the context of certain binaries.
     * Setting this to `false` can enhance security by mitigating privilege escalation attacks.
     *
     * A value of `null` indicates that the setting is not explicitly defined
     * and will defer to the default behavior of the container runtime.
     */
    var allowPrivilegeEscalation: Boolean? = null

    /**
     * Adds a capability to the security context configuration.
     * 
     * This method ensures that the capability is added to the list of capabilities 
     * managed by the `CapabilitiesSpecBuilder`. If the builder is not yet initialized, 
     * it will be created before adding the capability.
     *
     * @param capability The name of the capability to be added to the security context.
     */
    fun addCapability(capability: String) {
        if (capabilities == null) {
            capabilities = CapabilitiesSpecBuilder()
        }
        capabilities!!.add(capability)
    }

    /**
     * Adds one or more capabilities to the security context configuration.
     *
     * This method ensures that the specified capabilities are added to the list of
     * capabilities managed by the `CapabilitiesSpecBuilder`. If the builder is not yet
     * initialized, it will be created before adding the capabilities.
     *
     * @param capabilities Vararg parameter representing the names of the capabilities to be added.
     */
    fun addCapabilities(vararg capabilities: String) {
        if (this.capabilities == null) {
            this.capabilities = CapabilitiesSpecBuilder()
        }
        this.capabilities!!.addAll(*capabilities)
    }

    /**
     * Removes a specific capability from the security context configuration.
     *
     * This method ensures that the specified capability is removed from the list of 
     * dropped capabilities managed by the `CapabilitiesSpecBuilder`. If the builder 
     * is not initialized, it will be created before dropping the capability.
     *
     * @param capability The name of the capability to be removed from the security context.
     */
    fun dropCapability(capability: String) {
        if (capabilities == null) {
            capabilities = CapabilitiesSpecBuilder()
        }
        capabilities!!.drop(capability)
    }

    /**
     * Removes one or more capabilities from the security context configuration.
     *
     * This method ensures that the specified capabilities are removed from the list of dropped 
     * capabilities managed by the `CapabilitiesSpecBuilder`. If the builder is not yet initialized, 
     * it will be created before dropping the capabilities.
     *
     * @param capabilities Vararg parameter representing the names of the capabilities to be removed from the security context.
     */
    fun dropCapabilities(vararg capabilities: String) {
        if (this.capabilities == null) {
            this.capabilities = CapabilitiesSpecBuilder()
        }
        this.capabilities!!.dropAll(*capabilities)
    }

    /**
     * Configures the capabilities for the security context.
     *
     * This method initializes a `CapabilitiesSpecBuilder` and applies the provided
     * configuration to it. The configuration can include adding or dropping
     * specific capabilities, which are used to define the security context
     * of a container or pod.
     *
     * Example usage:
     * ```kotlin
     *     capabilities {
     *         add("NET_ADMIN")
     *         add("SYS_TIME")
     *         drop("ALL")
     *     }
     * ```
     *
     * @param prepare A lambda with receiver that defines the behavior for configuring 
     *                the capabilities using a `CapabilitiesSpecBuilder`.
     */
    fun capabilities(prepare: CapabilitiesSpecBuilder.() -> Unit) {
        capabilities = CapabilitiesSpecBuilder().apply(prepare)
    }

    /**
     * Builds a `SecurityContextSpec` instance based on the current configuration of the `SecurityContextSpecBuilder`.
     *
     * This method aggregates the values of the various security-related fields and constructs a `SecurityContextSpec` 
     * object to represent the final security context for a container or pod.
     *
     * @return A `SecurityContextSpec` object containing the configured security context.
     */
    override fun build(): ContainerSecurityContextSpec = ContainerSecurityContextSpec(
        runAsUser,
        runAsGroup,
        runAsNonRoot,
        seLinuxOptions?.build(),
        seccompProfile?.build(),
        appArmorProfile?.build(),
        windowsOptions?.build(),
        privileged,
        readOnlyRootFilesystem,
        allowPrivilegeEscalation,
        procMount,
        capabilities?.build()
    )

    /**
     * Builder class for constructing a `CapabilitiesSpec` object, which defines
     * capabilities to be added or dropped.
     *
     * This class provides methods for adding or dropping individual capabilities
     * as well as multiple capabilities in a single operation. It accumulates the 
     * specified capabilities into internal lists and constructs the final 
     * `CapabilitiesSpec` object via the `build` function.
     *
     * Instances of this class are created internally and are not intended to be
     * instantiated directly.
     */
    class CapabilitiesSpecBuilder internal constructor() {
        private var add: MutableList<String>? = null
        private var drop: MutableList<String>? = null

        /**
         * Adds a capability to the internal list of capabilities to be included.
         *
         * If the internal list is not initialized, it will be created before adding the capability.
         *
         * @param capability The capability to be added to the list.
         */
        fun add(capability: String) {
            if (add == null) {
                add = mutableListOf()
            }
            add!!.add(capability)
        }

        /**
         * Adds multiple capabilities to the internal list of capabilities to be included.
         *
         * Each capability provided in the `capabilities` parameter will be added to the list
         * using the `add` method.
         *
         * @param capabilities The capabilities to be added to the internal list of capabilities.
         */
        fun addAll(vararg capabilities: String) = capabilities.forEach { add(it) }

        /**
         * Removes a capability from the internal list of capabilities to be excluded.
         *
         * If the internal list is not initialized, it will be created before adding the capability
         * to ensure subsequent exclusions.
         *
         * @param capability The capability to be removed from the list.
         */
        fun drop(capability: String) {
            if (drop == null) {
                drop = mutableListOf()
            }
            drop!!.add(capability)
        }

        /**
         * Removes multiple capabilities from the internal list of capabilities to be excluded.
         *
         * Each capability provided in the `capabilities` parameter will be removed individually.
         *
         * @param capabilities A variable number of capability names to be removed.
         */
        fun dropAll(vararg capabilities: String) = capabilities.forEach { drop(it) }

        /**
         * Builds a new instance of the `CapabilitiesSpec` based on the current state of the `CapabilitiesSpecBuilder`.
         *
         * @return A `CapabilitiesSpec` instance containing the capabilities to be added and dropped as specified
         *         by the builder's internal state.
         */
        internal fun build(): ContainerSecurityContextSpec.CapabilitiesSpec =
            ContainerSecurityContextSpec.CapabilitiesSpec(add, drop)
    }
}
