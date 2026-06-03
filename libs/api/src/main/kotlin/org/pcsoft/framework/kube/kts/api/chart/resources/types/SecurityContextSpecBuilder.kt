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

import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodSecurityContextSpec.FSGroupChangePolicy
import org.pcsoft.framework.kube.kts.api.chart.resources.types.PodSecurityContextSpec.SupplementalGroupsPolicy
import org.pcsoft.framework.kube.kts.api.chart.resources.types.SecurityContextSpec.*

/**
 * A builder class for configuring and constructing a security context specification.
 *
 * This class provides a fluent API for customizing various security context options, such as
 * SELinux options, seccomp profile, AppArmor profile, and platform-specific features (e.g., Windows options).
 *
 * The configurations set in the builder are used to construct a security context specification
 * that enhances the security of containers or pods by applying fine-grained access control policies.
 */
sealed class SecurityContextSpecBuilder<T : SecurityContextSpec> {
    protected var seLinuxOptions: SELinuxOptionsSpecBuilder? = null; private set
    protected var seccompProfile: ProfileSpecBuilder? = null; private set
    protected var appArmorProfile: ProfileSpecBuilder? = null; private set
    protected var windowsOptions: WindowsOptionsSpecBuilder? = null; private set

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
     * Builds and returns the constructed instance of type `T` based on the configurations
     * applied to the `SecurityContextSpecBuilder`. This method should be called after 
     * all the desired configurations have been specified.
     *
     * @return The constructed instance of type `T` representing the configured security context.
     */
    internal abstract fun build(): T

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

/**
 * A builder class for constructing a `PodSecurityContextSpec` instance.
 *
 * This builder provides methods to configure the various attributes of a pod's security context.
 * Common attributes include file system group settings, supplemental group policies, and system-level
 * configurations like sysctls.
 *
 * The `PodSecurityContextSpecBuilder` allows for precise customization and is best suited for scenarios
 * where programmatic creation of security context specifications is required.
 *
 * Constructors for this class are internal, and it inherits from `SecurityContextSpecBuilder` to retain
 * additional shared configuration behaviors.
 */
class PodSecurityContextSpecBuilder internal constructor() : SecurityContextSpecBuilder<PodSecurityContextSpec>() {
    private var supplementalGroups: MutableList<Long>? = null
    private var sysctls: MutableMap<String, String>? = null

    /**
     * The fsGroup field defines a special supplemental group that applies to all files
     * and directories created by the container's processes. When specified, the owner
     * group ID of any files created will be set to this ID, effectively assigning the file
     * to the specified group.
     *
     * This is typically used to allow shared file access between processes that belong
     * to different containers within the same Pod.
     *
     * A null value indicates that no specific fsGroup is set, and default group ownership
     * behavior will apply.
     */
    var fsGroup: Long? = null

    /**
     * Represents the policy that governs the behavior of changing the file system group
     * for a pod's volume mounts within the security context.
     *
     * Determines how and when the file system group should be applied to ensure the desired 
     * security and operational settings are enforced. The behavior is controlled by the 
     * `FSGroupChangePolicy` enum, which provides predefined policies like applying the 
     * group always or only when there is a mismatch.
     *
     * This property is optional and is particularly relevant in scenarios where the 
     * handling of file system group changes must be explicitly defined to meet specific 
     * security or operational requirements for the pod's volume configuration.
     */
    var fsGroupChangePolicy: FSGroupChangePolicy? = null

    /**
     * Specifies the policy for handling supplemental groups within the pod's security context.
     * This determines how supplemental groups are treated in relation to the pod's settings 
     * and any existing groups defined at the node or cluster level.
     */
    var supplementalGroupsPolicy: SupplementalGroupsPolicy? = null

    /**
     * Adds a supplemental group to the list of supplemental groups.
     *
     * @param value The supplemental group ID to be added.
     */
    fun addSupplementalGroup(value: Long) {
        if (supplementalGroups == null) {
            supplementalGroups = mutableListOf()
        }
        supplementalGroups!!.add(value)
    }

    /**
     * Adds one or more supplemental group IDs to the list of supplemental groups.
     *
     * @param values Variable number of supplemental group IDs to be added.
     */
    fun addSupplementalGroups(vararg values: Long) {
        if (supplementalGroups == null) {
            supplementalGroups = mutableListOf()
        }
        supplementalGroups!!.addAll(values.toList())
    }

    /**
     * Configures the list of supplemental group IDs to be associated with the pod's security context.
     *
     * This method allows the user to define multiple supplemental group IDs through a configuration
     * block using the `SupplementalGroupListBuilder`. Supplemental group IDs are used for setting
     * file access permissions within a pod.
     *
     * Example usage:
     * ```kotlin
     *     supplementalGroups {
     *         group(1000L)
     *         groups(2000L, 3000L)
     *     }
     * ```
     *
     * @param prepare A lambda with `SupplementalGroupListBuilder` as its receiver to define 
     *                supplemental group IDs.
     */
    fun supplementalGroups(prepare: SupplementalGroupListBuilder.() -> Unit) {
        SupplementalGroupListBuilder().apply(prepare)
    }

    /**
     * Adds a sysctl configuration to the pod's security context.
     *
     * Sysctls are kernel parameters that can be configured at runtime to control
     * various aspects of the system behavior within the pod.
     *
     * @param key The name of the sysctl parameter to configure.
     * @param value The value to set for the specified sysctl parameter.
     */
    fun addSysctl(key: String, value: String) {
        if (sysctls == null) {
            sysctls = mutableMapOf()
        }
        sysctls!![key] = value
    }

    /**
     * Configures sysctl parameters for the pod's security context.
     *
     * This method allows defining multiple sysctl parameters through a configuration
     * block using the `SysctlMapBuilder`. Sysctls are kernel parameters that control
     * various aspects of system behavior within the pod.
     *
     * Example usage:
     * ```kotlin
     *     sysctls {
     *         sysctl("net.ipv4.ip_forward", "1")
     *         sysctl("kernel.shm_rmid_forced", "0")
     *     }
     * ```
     *
     * @param prepare A lambda with `SysctlMapBuilder` as its receiver to define
     *                sysctl parameters.
     */
    fun sysctls(prepare: SysctlMapBuilder.() -> Unit) {
        SysctlMapBuilder().apply(prepare)
    }

    /**
     * Builds and returns a new instance of `PodSecurityContextSpec` based on the current configuration
     * in the `PodSecurityContextSpecBuilder`.
     *
     * @return A `PodSecurityContextSpec` object that encapsulates the security context settings
     * such as user ID, group ID, SELinux options, Seccomp profile, AppArmor profile,
     * Windows-specific options, file system group settings, supplemental groups, and sysctl parameters.
     */
    override fun build(): PodSecurityContextSpec = PodSecurityContextSpec(
        runAsUser,
        runAsGroup,
        runAsNonRoot,
        seLinuxOptions?.build(),
        seccompProfile?.build(),
        appArmorProfile?.build(),
        windowsOptions?.build(),
        fsGroup,
        fsGroupChangePolicy,
        supplementalGroups,
        supplementalGroupsPolicy,
        sysctls,
    )

    /**
     * A builder class for configuring a list of supplemental group IDs associated with a pod's security context.
     *
     * This class provides methods to add individual or multiple supplemental group IDs, which are used to set
     * file access permissions within a pod. The builder can be used within a configuration block to define the
     * desired group IDs.
     */
    inner class SupplementalGroupListBuilder internal constructor() {
        /**
         * Adds a single supplemental group ID to the list of supplemental groups.
         *
         * Supplemental group IDs are used to configure file access permissions
         * within a pod's security context.
         *
         * @param value The supplemental group ID to add.
         */
        fun group(value: Long) = addSupplementalGroup(value)

        /**
         * Adds multiple supplemental group IDs to the builder's list of group IDs.
         *
         * Supplemental group IDs are used to configure file access permissions
         * within a pod's security context.
         *
         * @param values Variable number of supplemental group IDs to be added.
         */
        fun groups(vararg values: Long) = addSupplementalGroups(*values)
    }

    /**
     * A builder class for defining sysctl parameters in the pod's security context.
     *
     * Sysctls are kernel parameters that control various aspects of system behavior
     * within the pod. This builder provides a DSL for adding multiple sysctl
     * configurations through a fluent interface.
     *
     * This class is intended for internal use and is not exposed publicly.
     */
    inner class SysctlMapBuilder internal constructor() {
        /**
         * Configures a sysctl parameter for the pod's security context.
         *
         * This function uses `addSysctl` to define a specific kernel parameter
         * and its value, which controls certain aspects of system behavior
         * within the pod.
         *
         * @param key The name of the sysctl parameter to configure.
         * @param value The value to assign to the specified sysctl parameter.
         */
        fun sysctl(key: String, value: String) = addSysctl(key, value)
    }
}