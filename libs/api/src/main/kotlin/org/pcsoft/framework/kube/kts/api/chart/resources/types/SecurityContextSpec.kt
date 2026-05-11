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

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

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
 * @param privileged Whether to run the container with elevated privileges.
 * @param readOnlyRootFilesystem Whether to mount the root filesystem as read-only.
 * @param allowPrivilegeEscalation Whether to allow privilege escalation within the container.
 * @param procMount The type of process mount to use for the container.
 * @param capabilities The capabilities to assign to the container.
 * @param seLinuxOptions The SELinux options for the container.
 * @param seccompProfile The seccomp profile to apply to the container.
 * @param appArmorProfile The AppArmor profile to apply to the container.
 * @param windowsOptions The Windows-specific options for the container.
 */
@NoArgs
data class SecurityContextSpec(
    val runAsUser: Long?,
    val runAsGroup: Long?,
    val runAsNonRoot: Boolean?,
    val privileged: Boolean?,
    val readOnlyRootFilesystem: Boolean?,
    val allowPrivilegeEscalation: Boolean?,
    val procMount: ProcMountType?,
    val capabilities: CapabilitiesSpec?,
    val seLinuxOptions: SELinuxOptionsSpec?,
    val seccompProfile: ProfileSpec?,
    val appArmorProfile: ProfileSpec?,
    val windowsOptions: WindowsOptionsSpec?,
) {
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
