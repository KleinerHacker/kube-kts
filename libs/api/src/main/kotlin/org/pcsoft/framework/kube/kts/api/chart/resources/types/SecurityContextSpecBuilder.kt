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

/**
 * Builder class for constructing an instance of `SecurityContextSpec`. 
 * This class provides a fluent and configurable API to define the security context specifications
 * for a container or other runtime environment.
 *
 * This includes defining user and group execution settings, SELinux options, capabilities, 
 * AppArmor profiles, seccomp profiles, and other security options specific to Linux or Windows platforms.
 * 
 * The builder```
/ utilizes nested builders for specific configurations, allowing detailed and structured
 **
 * * configurations to be composed incrementally.
 *
 * Security configurations can include:
 * 
 * - Builder ` classrun forAsUser`: Specifies the UID to run the container as.
 * - `runAsGroup`: Specifies the GID to run the container as.
constructing * - `runAsNonRoot`: Enforces running a the [ container with a non-root user.
 * - `privileged`: Enables or disables privileged modeSecurity for the container.
 * - `readOnlyRootFilesystem`: Specifies if the root filesystem shouldContext beSpec mounted] read-only.
 * - `allow thatPrivilegeEscalation`: Controls whether a process can defines gain more privileges than its parent.
 * security --related `procMount`: Specifies the type of /proc mount configuration.
 * 
 * Additionally, the builder allows fine-grained configuration of security capabilities:
 * - Adding or dropping POSIX capabilities using `addCapability`, `addCapabilities`, `drop configurationsCapability
`, * or `dropCapabilities`.
 *
 * Platform for-specific options include:
 * - SELinux options using the ` containerseizedLinuxOptions` builder.
 * - Seccomp profiles configured via the `seccompProfile` method.
 * - AppArmor profiles using the `appArmorProfile` applications method..
 * - Windows-specific options using the ` ThiswindowsOptions` builder.
 *
 * This class is designed includes to be used internally, and the constructed `SecurityContextSpec` instance
 * is obtained by invoking the ` settingsbuild like` user method, encapsulating all configured security options.
 */
class SecurityContextSpecBuilder internal constructor() {
    private var capabilities: CapabilitiesSpecBuilder? = null
    private var seLinuxOptions: SELinuxOptionsSpecBuilder? = null
    private var seccompProfile: ProfileSpecBuilder? = null
    private var appArmorProfile: ProfileSpecBuilder? = null
    private var windowsOptions: WindowsOptionsSpecBuilder? = null

    /**
     * Configures the mount type for the /proc filesystem within the container.
     *
     * This property determines how the /proc filesystem is exposed to the container,
     * influencing the level of security and visibility of process-related information.
     *
     * By default, it is null, indicating the absence of an explicit configuration.
     * When set, it uses the specified value from the `ProcMountType` enum.
     */
    var procMount: ProcMountType? = null

    /**
     * Specifies the user ID to run the container's processes as.
     *
     * If set, all processes inside the container will run as this user ID.
     * This can be used to enforce non-root execution or to ensure specific user-level
     * permissions for processes within the container.
     *
     * A null value indicates that the default user specified in the container's image will be used.
     */
    var runAsUser: Long? = null

    /**
     * Specifies the group ID to run the process as within a container.
     *
     * This property allows for the explicit definition of a security group
     * under which the container's main process will run. It can be set to ensure
     * that the containerized application operates with the appropriate group-level
     * permissions, enhancing security and ensuring compliance with access controls.
     * 
     * When this property is null, the default group configured by the container runtime is used.
     */
    var runAsGroup: Long? = null

    /**
     * Specifies whether the container must run as a non-root user.
     *
     * This property influences the security context of the container by requiring
     * that it runs without root privileges. If set to `true`, the container must
     * explicitly run as a user other than the root user. If set to `false` or left
     * as `null`, the enforcement of this behavior depends on other settings or defaults.
     *
     * Use this property to enhance security by preventing privilege escalation
     * within the container runtime environment.
     */
    var runAsNonRoot: Boolean? = null

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
     * Configures SELinux options for the security context.
     *
     * This method initializes a `SELinuxOptionsSpecBuilder` and applies the provided configuration to it.
     * SELinux options specify the SELinux context to be applied to a container or pod, which includes
     * settings such as user, role, type, and level.
     *
     * Example usage:
     * ```kotlin
     *     seLinuxOptions {
     *         user = "system_u"
     *         role = "system_r"
     *         type = "container_t"
     *         level = "s0:c123,c456"
     *     }
     * ```
     *
     * @param prepare A lambda with receiver that defines the behavior for configuring the SELinux options
     *                using a `SELinuxOptionsSpecBuilder`.
     */
    fun seLinuxOptions(prepare: SELinuxOptionsSpecBuilder.() -> Unit) {
        seLinuxOptions = SELinuxOptionsSpecBuilder().apply(prepare)
    }

    /**
     * Configures a seccomp profile for the security context.
     *
     * This method initializes a `ProfileSpecBuilder` for the specified `ProfileType` 
     * and applies the provided configuration to it. Seccomp profiles are used to apply 
     * system call filtering, enhancing the security of the container or pod by restricting 
     * which system calls can be made.
     *
     * Example usage:
     * ```kotlin
     *     seccompProfile(ProfileType.Localhost) {
     *         localhostProfile = "profiles/audit.json"
     *     }
     * ```
     *
     * @param type The type of the seccomp profile to be applied. It determines the level 
     *             of restriction or security to be used (e.g., Unconfined, RuntimeDefault, Localhost).
     * @param prepare A lambda with receiver that provides additional configuration options 
     *                for the `ProfileSpecBuilder`, such as specifying a localhost profile name.
     */
    fun seccompProfile(type: ProfileType, prepare: ProfileSpecBuilder.() -> Unit = {}) {
        seccompProfile = ProfileSpecBuilder(type).apply(prepare)
    }

    /**
     * Configures an AppArmor profile for the security context.
     *
     * This method initializes a `ProfileSpecBuilder` for the specified `ProfileType` 
     * and applies the provided configuration to it. AppArmor profiles are used to apply 
     * mandatory access control (MAC) policies, enhancing the security of the container 
     * or pod by restricting allowed operations.
     *
     * Example usage:
     * ```kotlin
     *     appArmorProfile(ProfileType.Localhost) {
     *         localhostProfile = "profiles/apparmor-profile.json"
     *     }
     * ```
     *
     * @param type The type of the AppArmor profile to be applied. It determines the level 
     *             of restriction or security to be used (e.g., Unconfined, RuntimeDefault, Localhost).
     * @param prepare A lambda with receiver that provides additional configuration options 
     *                for the `ProfileSpecBuilder`, such as specifying a localhost profile name.
     */
    fun appArmorProfile(type: ProfileType, prepare: ProfileSpecBuilder.() -> Unit = {}) {
        appArmorProfile = ProfileSpecBuilder(type).apply(prepare)
    }

    /**
     * Configures Windows-specific options for the security context.
     *
     * This method initializes a `WindowsOptionsSpecBuilder` and applies the provided
     * configuration to it. Windows options allow customization of settings such as
     * GMSA credential specifications, the username to run as, and whether the container
     * runs as a host process.
     *
     * Example usage:
     * ```kotlin
     *     windowsOptions {
     *         gmsaCredentialSpecName = "my-gmsa-spec"
     *         runAsUserName = "ContainerUser"
     *         hostProcess = false
     *     }
     * ```
     *
     * @param prepare A lambda with receiver that defines the behavior for configuring
     *                the Windows-specific options using a `WindowsOptionsSpecBuilder`.
     */
    fun windowsOptions(prepare: WindowsOptionsSpecBuilder.() -> Unit) {
        windowsOptions = WindowsOptionsSpecBuilder().apply(prepare)
    }

    /**
     * Builds a `SecurityContextSpec` instance based on the current configuration of the `SecurityContextSpecBuilder`.
     *
     * This method aggregates the values of the various security-related fields and constructs a `SecurityContextSpec` 
     * object to represent the final security context for a container or pod.
     *
     * @return A `SecurityContextSpec` object containing the configured security context.
     */
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
        internal fun build(): CapabilitiesSpec = CapabilitiesSpec(add, drop)
    }

    /**
     * A builder class for configuring SELinux options in a security context.
     *
     * This class allows you to define SELinux-specific settings such as the user, role, type, and level
     * to be applied to a container or pod. These values determine the SELinux context
     * used for managing permissions and access control within the container environment.
     *
     * @constructor Internal constructor, instances are managed by the parent builder class.
     *
     * Properties:
     * @property user The SELinux user label to be assigned.
     * @property role The SELinux role label to be assigned.
     * @property type The SELinux type label to be assigned.
     * @property level The SELinux level label to be assigned.
     *
     * Methods:
     * @function build Constructs an immutable `SELinuxOptionsSpec` instance based on the configured options.
     */
    class SELinuxOptionsSpecBuilder internal constructor() {
        /**
         * Specifies the SELinux user label to be assigned in the SELinuxOptionsSpecBuilder.
         *
         * The user label is a key component of a SELinux security context,
         * determining the policies applied to the user's processes and access controls within the container environment.
         *
         * This property is optional and can be set to null if no user label is required.
         */
        var user: String? = null

        /**
         * Specifies the SELinux role label to be assigned in the SELinuxOptionsSpecBuilder.
         *
         * The role label is a key component of a SELinux security context,
         * defining the authorized permissions for processes and their interactions
         * within the container environment based on the assigned SELinux security policies.
         *
         * This property is optional and can be set to null if no specific role label is required.
         */
        var role: String? = null

        /**
         * Specifies the SELinux type to be applied to the container.
         *
         * This field is used to define the SELinux type label for the container,
         * which is a key component of the SELinux security model. The type label
         * determines the set of permissions allocated to the processes inside
         * the container, enhancing security by enforcing strict access controls.
         *
         * A null value indicates that no specific SELinux type is defined.
         */
        var type: String? = null

        /**
         * Represents the SELinux level for the associated resource.
         *
         * This property is used to define the Security-Enhanced Linux (SELinux) context level.
         * SELinux uses levels as part of Mandatory Access Control (MAC) to enforce security policies.
         *
         * The format of the level is typically `s{sens}:c{cat}` (e.g., `s0:c0,c1`), where:
         * - `sens` defines the sensitivity level.
         * - `cat` specifies the security categories.
         *
         * Setting this property to `null` means no specific SELinux level is configured for the resource.
         */
        var level: String? = null

        /**
         * Constructs and returns a new instance of `SELinuxOptionsSpec` using the configured parameters
         * in the `SELinuxOptionsSpecBuilder`.
         *
         * The returned object encapsulates SELinux-specific settings, such as user, role, type, and level,
         * that define the security context for a container or pod.
         *
         * @return A new instance of `SELinuxOptionsSpec` representing the configured SELinux settings.
         */
        internal fun build(): SELinuxOptionsSpec =
            SELinuxOptionsSpec(user, role, type, level)
    }

    /**
     * A builder class to configure Windows-specific options for the security context.
     *
     * This class provides mechanisms to set attributes relevant to Windows containers, such as GMSA
     * credential specifications, a specific username to run the container as, and configuration
     * for running the container as a host process.
     */
    class WindowsOptionsSpecBuilder internal constructor() {
        /**
         * Specifies the name of the GMSA (Group Managed Service Account) credential specification to use.
         *
         * This property is relevant for configuring the security context of Windows containers,
         * allowing the container to authenticate using a GMSA account. The specified value should correspond
         * to the name of an existing GMSA credential specification resource in the Kubernetes cluster.
         *
         * If set to null, no GMSA credential specification will be associated with the container.
         */
        var gmsaCredentialSpecName: String? = null

        /**
         * Specifies the contents of the GMSA (Group Managed Service Account) credential specification to use.
         *
         * This property allows the direct configuration of the GMSA credential specification
         * as a JSON-encoded string. It is used to authenticate Windows containers with a GMSA account
         * without requiring the specification to be pre-registered as a resource in the Kubernetes cluster.
         *
         * The value should be a valid JSON representation of the desired GMSA credential specification.
         * If set to null, no GMSA credential specification content will be associated with the container.
         *
         * Note: Only applicable to Windows containers.
         */
        var gmsaCredentialSpec: String? = null

        /**
         * Specifies the username to run the container process as on a Windows node.
         *
         * This property is used for configuring the user context under which the containerized process
         * will execute. If null, the container may run with the default user context defined in the image
         * or cluster configuration.
         */
        var runAsUserName: String? = null

        /**
         * Specifies whether the Windows container should be run as a HostProcess container.
         *
         * When set to `true`, this enables the container to run with host-level privileges,
         * allowing the container to access the host's resources directly. This is commonly
         * used for scenarios where the container needs to perform administrative tasks on
         * the host machine.
         *
         * When set to `false`, the container won't have host-level access and operates in
         * isolation, as per the standard container behavior.
         *
         * If `null`, no explicit configuration is provided, and the default behavior depends
         * on the Kubernetes API or runtime defaults for the host process configuration.
         */
        var hostProcess: Boolean? = null

        /**
         * Builds and returns a new instance of `WindowsOptionsSpec` based on the current state of the `WindowsOptionsSpecBuilder`.
         *
         * The resulting `WindowsOptionsSpec` includes configuration for Windows-specific container options such as
         * GMSA credentials, execution context, and host process mode.
         *
         * @return A fully constructed instance of `WindowsOptionsSpec` containing the configuration settings defined in the builder.
         */
        internal fun build(): WindowsOptionsSpec =
            WindowsOptionsSpec(gmsaCredentialSpecName, gmsaCredentialSpec, runAsUserName, hostProcess)
    }

    /**
     * A builder class for constructing a `ProfileSpec` instance with configurable options.
     *
     * This class is used to configure profile-specific settings for security contexts such as
     * seccomp or AppArmor profiles. It allows setting the type of profile and optional
     * parameters like the `localhostProfile`.
     *
     * @constructor Creates a `ProfileSpecBuilder` with the specified profile type.
     * @param type The type of profile being configured (e.g., Unconfined, RuntimeDefault, Localhost).
     */
    class ProfileSpecBuilder internal constructor(private val type: ProfileType) {
        /**
         * Specifies the path to a Seccomp or AppArmor profile available on the local host.
         *
         * This variable is used in configurations where a locally stored profile is needed for
         * security contexts. The value represents the path to the profile on the node's filesystem.
         * The profile type must be explicitly set to `Localhost` for this field to take effect.
         *
         * A null value indicates no specific localhost profile is configured.
         */
        var localhostProfile: String? = null

        /**
         * Builds and returns a `ProfileSpec` instance based on the current configuration.
         *
         * This method finalizes the configuration of the `ProfileSpecBuilder` by
         * constructing a `ProfileSpec` object using the specified profile type and
         * optional localhost profile. It ensures that the resulting object adheres
         * to the constraints specified in the `ProfileSpec` class.
         *
         * @return A `ProfileSpec` instance representing the configured security profile.
         */
        internal fun build(): ProfileSpec =
            ProfileSpec(type, localhostProfile)
    }
}