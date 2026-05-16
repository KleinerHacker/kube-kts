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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.MapToNameValueDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.MapToNameValueSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Represents the security context for a container, defining various security-related configurations.
 *
 * This specification includes settings such as user and group IDs, privilege escalation controls,
 * filesystem permissions, capabilities, SELinux options, seccomp profiles, AppArmor profiles,
 * and Windows-specific options. It is used to enforce security policies at the container level.
 *
 * @param runAsUser The user ID to run the container as.
 * @param runAsGroup The group ID to run the container as.
 * @param runAsNonRoot Whether to run the container as a non-root user.
 * @param seLinuxOptions The SELinux options for the container.
 * @param seccompProfile The seccomp profile to apply to the container.
 * @param appArmorProfile The AppArmor profile to apply to the container.
 * @param windowsOptions The Windows-specific options for the container.
 */
@NoArgs
sealed class SecurityContextSpec(
    val runAsUser: Long?,
    val runAsGroup: Long?,
    val runAsNonRoot: Boolean?,
    val seLinuxOptions: SELinuxOptionsSpec?,
    val seccompProfile: ProfileSpec?,
    val appArmorProfile: ProfileSpec?,
    val windowsOptions: WindowsOptionsSpec?,
) {
    /**
     * Defines the types of security profiles that can be applied to a pod or container.
     *
     * This enum class specifies different security context profile types, such as unconfined (no restrictions),
     * runtime default (default security settings), and localhost (restricted to local host).
     */
    @Suppress("unused")
    enum class ProfileType {
        /**
         * Represents the unconfined security profile type, indicating no restrictions or limitations on the pod or
         * container's access and privileges.
         *
         * This profile allows unrestricted operations within the Kubernetes environment, which may pose potential
         * security risks if not managed properly.
         */
        Unconfined,

        /**
         * Represents the runtime default security profile type, which applies the default security settings
         * defined by the container runtime environment. This profile provides a balance between security
         * and functionality, enforcing restrictions that are typically recommended for most workloads while
         * allowing necessary operations to run containers effectively.
         *
         * Unlike [Unconfined], which imposes no restrictions, or [Localhost], which restricts access to the local host,
         * this profile follows the runtime's default security policies, such as those defined by gVisor, Kata Containers,
         * or other container runtimes. It is suitable for environments where a moderate level of security is desired
         * without sacrificing essential container operations.
         */
        RuntimeDefault,

        /**
         * Represents a security profile type that restricts access to the local host only.
         *
         * This profile enforces strict security measures, limiting container operations to interactions with the localhost,
         * preventing any external network communication or access. It is suitable for environments where maximum isolation
         * and security are required, such as sensitive workloads or compliance-driven deployments.
         */
        Localhost
    }

    /**
     * Represents SELinux options for a pod or container, allowing fine-grained control over the security context.
     *
     * This class is used to specify Security Enhanced Linux (SELinux) settings such as user, role, type, and level,
     * which determine the access permissions and security policies applied to the running process. All specified values
     * must be non-blank if provided.
     *
     * @property user The SELinux user label for the container. Must not be blank if provided.
     * @property role The SELinux role label for the container. Must not be blank if provided.
     * @property type The SELinux type label for the container. Must not be blank if provided.
     * @property level The SELinux level label for the container. Must not be blank if provided.
     */
    @NoArgs
    data class SELinuxOptionsSpec(
        val user: String?,
        val role: String?,
        val type: String?,
        val level: String?,
    ) {
        init {
            user?.let { require(it.isNotBlank()) { "User must not be blank" } }
            role?.let { require(it.isNotBlank()) { "Role must not be blank" } }
            type?.let { require(it.isNotBlank()) { "Type must not be blank" } }
            level?.let { require(it.isNotBlank()) { "Level must not be blank" } }
        }
    }

    /**
     * Specifies Windows-specific container options for security context configuration.
     *
     * This class defines parameters that control how a container runs on Windows hosts,
     * including Group Managed Service Account (GMSA) credentials, user execution context,
     * and host process mode. All string properties must not be blank if provided.
     *
     * @property gmsaCredentialSpecName The name of the GMSA credential spec to use for authentication.
     * @property gmsaCredentialSpec The GMSA credential spec content itself.
     * @property runAsUserName The username to run the container as on Windows.
     * @property hostProcess Whether the container should run in host process mode (requires privileged access).
     */
    @NoArgs
    data class WindowsOptionsSpec(
        val gmsaCredentialSpecName: String?,
        val gmsaCredentialSpec: String?,
        val runAsUserName: String?,
        val hostProcess: Boolean?,
    ) {
        init {
            gmsaCredentialSpecName?.let { require(it.isNotBlank()) { "GMSA credential spec name must not be blank" } }
            //TODO: Spec as JSON?
            gmsaCredentialSpec?.let { require(it.isNotBlank()) { "GMSA credential spec must not be blank" } }
            runAsUserName?.let { require(it.isNotBlank()) { "Run as user name must not be blank" } }
        }
    }

    /**
     * Specifies the security profile configuration for a pod or container.
     *
     * This data class defines the type of security profile to be applied and, if applicable,
     * the localhost profile string. It enforces validation rules to ensure that when the [type]
     * is set to [ProfileType.Localhost], the [localhostProfile] must not be null or blank.
     *
     * @property type The type of security profile to apply (e.g., unconfined, runtime default, or localhost).
     * @property localhostProfile A string representing the localhost profile, required only when [type]
     * is [ProfileType.Localhost]. Must not be null or blank in such cases.
     */
    @NoArgs
    data class ProfileSpec(
        val type: ProfileType,
        val localhostProfile: String?,
    ) {
        init {
            if (type == ProfileType.Localhost) {
                require(localhostProfile != null) { "Localhost profile must not be null" }
            }
            localhostProfile?.let { require(it.isNotBlank()) { "Localhost profile must not be blank" } }
        }
    }
}

/**
 * Specifies the security context settings for a container within a Kubernetes pod.
 *
 * This class provides options to configure the security-related attributes of a container,
 * including file system permissions, user and group execution context, privilege levels,
 * and support for SELinux, Seccomp, AppArmor profiles, and Windows-specific settings.
 * The configuration focuses on ensuring containers maintain the necessary security restrictions
 * while allowing for fine-grained control of their behavior.
 *
 * @constructor Creates a new instance of the ContainerSecurityContextSpec class.
 *
 * @param runAsUser The user ID to run the container process as. Can be null.
 * @param runAsGroup The group ID to run the container process as. Can be null.
 * @param runAsNonRoot Indicates whether the container process should run as a non-root user. Can be null.
 * @param seLinuxOptions SELinux options defining the SELinux context of the container. Can be null.
 * @param seccompProfile Seccomp profile details to enforce syscall policy. Can be null.
 * @param appArmorProfile AppArmor profile details to configure process confinement. Can be null.
 * @param windowsOptions Windows-specific container options for security settings. Can be null.
 * @param privileged Determines whether the container should run in privileged mode. Can be null.
 * @param readOnlyRootFilesystem Indicates if the container's root filesystem should be mounted as read-only. Can be null.
 * @param allowPrivilegeEscalation Indicates if the container process is allowed to gain additional privileges. Can be null.
 * @param procMount Specifies the type of mount configuration for the /proc filesystem. Can be null.
 * @param capabilities Defines the capabilities to add or drop for the container process. Can be null.
 */
@NoArgs
class ContainerSecurityContextSpec(
    runAsUser: Long?,
    runAsGroup: Long?,
    runAsNonRoot: Boolean?,
    seLinuxOptions: SELinuxOptionsSpec?,
    seccompProfile: ProfileSpec?,
    appArmorProfile: ProfileSpec?,
    windowsOptions: WindowsOptionsSpec?,
    val privileged: Boolean?,
    val readOnlyRootFilesystem: Boolean?,
    val allowPrivilegeEscalation: Boolean?,
    val procMount: ProcMountType?,
    val capabilities: CapabilitiesSpec?,
) : SecurityContextSpec(runAsUser, runAsGroup, runAsNonRoot, seLinuxOptions, seccompProfile, appArmorProfile, windowsOptions) {
    /**
     * Represents the type of mount configuration for the /proc filesystem within a container.
     *
     * This enum defines how the /proc filesystem is exposed to containers, affecting security and visibility of
     * process information.
     */
    @Suppress("unused")
    enum class ProcMountType {
        /**
         * Represents the default mount configuration for the /proc filesystem within a container.
         *
         * This option provides standard visibility of process information while maintaining security restrictions.
         * It is suitable for most use cases where containers need access to basic process details without exposing
         * sensitive kernel data.
         */
        Default,

        /**
         * Represents an unmasked mount configuration for the /proc filesystem within a container.
         *
         * This option provides unrestricted visibility of process information, including sensitive kernel data.
         * It should be used with caution as it may expose security-sensitive information about the host system.
         */
        Unmasked
    }

    /**
     * Represents a specification for Linux capabilities to add or drop from a container.
     *
     * This class is used to define which Linux capabilities should be added to or removed from
     * the container's process. Capabilities allow fine-grained control over the privileges of a process,
     * enabling more secure container configurations by restricting unnecessary permissions.
     *
     * @property add A list of capability names to add to the container. Can be null if no capabilities are to be added.
     *                Each entry in the list must not be blank.
     * @property drop A list of capability names to remove from the container. Can be null if no capabilities are to be dropped.
     *                Each entry in the list must not be blank.
     */
    @NoArgs
    data class CapabilitiesSpec(
        val add: List<String>?,
        val drop: List<String>?,
    ) {
        init {
            add?.let { require(it.all { it.isNotBlank() }) { "Add must not contain blank values" } }
            drop?.let { require(it.all { it.isNotBlank() }) { "Drop must not contain blank values" } }
        }
    }
}

/**
 * Defines the pod-level security context, encapsulating security-related configurations
 * for all containers that run within the pod. This specification allows fine-grained
 * control over various security mechanisms at the pod scope.
 *
 * @constructor Creates a new instance of `PodSecurityContextSpec` and applies the provided
 * security configurations.
 *
 * @param runAsUser The user ID to run processes as. If specified, overrides the UID
 * set for containers.
 * @param runAsGroup The group ID to run processes as. If specified, overrides the GID
 * set for containers.
 * @param runAsNonRoot Indicates if the container must run as a non-root user. If true,
 * this requires `runAsUser` to be configured to a non-zero value.
 * @param seLinuxOptions SELinux options specifying the user, role, type, and level for SELinux contexts.
 * @param seccompProfile Security profile configuration for Seccomp to restrict the system call
 * surface available to the container.
 * @param appArmorProfile Security profile configuration for AppArmor to enforce mandatory
 * access control.
 * @param windowsOptions Windows-specific options for configuring the execution of containers
 * on Windows nodes.
 * @param fsGroup The group ID for all files that the pod's processes can access. This is applied
 * to all volumes mounted for the pod.
 * @param fsGroupChangePolicy Defines the behavior for modifying file ownership and permissions
 * of volumes when `fsGroup` is specified.
 * @param supplementalGroups A list of additional group IDs to add to the container's primary
 * process's group membership.
 * @param supplementalGroupsPolicy Defines the policy for handling supplemental groups, such as
 * whether they should strictly match or be merged.
 * @param sysctls A map of system control parameters to apply to the pod. These parameters
 * can modify kernel-level configurations.
 */
@NoArgs
class PodSecurityContextSpec(
    runAsUser: Long?,
    runAsGroup: Long?,
    runAsNonRoot: Boolean?,
    seLinuxOptions: SELinuxOptionsSpec?,
    seccompProfile: ProfileSpec?,
    appArmorProfile: ProfileSpec?,
    windowsOptions: WindowsOptionsSpec?,
    val fsGroup: Long?,
    val fsGroupChangePolicy: FSGroupChangePolicy?,
    val supplementalGroups: List<Long>?,
    val supplementalGroupsPolicy: SupplementalGroupsPolicy?,
    @field:JsonSerialize(using = MapToNameValueSerializer::class)
    @field:JsonDeserialize(using = MapToNameValueDeserializer::class)
    val sysctls: Map<String, String>?,
) : SecurityContextSpec(runAsUser, runAsGroup, runAsNonRoot, seLinuxOptions, seccompProfile, appArmorProfile, windowsOptions) {

    /**
     * Represents the policy that governs the behavior of changing the file system group
     * for a pod's volume mounts.
     *
     * This enum is typically used within the context of pod security configurations to
     * dictate how the file system group should be applied when volumes are mounted.
     */
    @Suppress("unused")
    enum class FSGroupChangePolicy {
        /**
         * Enum value that specifies the policy to apply the file system group only when a mismatch
         * is detected between the existing file system group and the desired configuration.
         *
         * This policy can be used to ensure that unnecessary changes to the file system group
         * do not occur unless strictly required.
         */
        OnRootMismatch,

        /**
         * Enum value that specifies the policy to always apply the file system group
         * for the volumes, regardless of whether there is a mismatch with the existing
         * file system group.
         *
         * This policy ensures that the desired file system group configuration is
         * enforced consistently.
         */
        Always
    }

    /**
     * Determines the policy for handling supplemental groups within the pod's security context.
     */
    @Suppress("unused")
    enum class SupplementalGroupsPolicy {
        /**
         * Represents the `Merge` policy option within the `SupplementalGroupsPolicy` enum.
         *
         * This policy dictates that supplemental groups specified in the pod's security context
         * will be merged with any existing groups defined at the node or cluster level.
         * It ensures that the groups are combined rather than replaced, allowing for
         * additive group assignments.
         */
        Merge,

        /**
         * Represents the `Strict` policy option within the `SupplementalGroupsPolicy` enum.
         *
         * This policy enforces strict adherence to the supplemental groups specified in the pod's
         * security context. No additional groups defined at the node or cluster level
         * are merged or supplemented. The pod's groups are treated as authoritative.
         */
        Strict
    }
}
