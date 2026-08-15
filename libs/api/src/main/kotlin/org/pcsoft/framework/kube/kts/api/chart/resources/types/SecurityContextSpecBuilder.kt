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
