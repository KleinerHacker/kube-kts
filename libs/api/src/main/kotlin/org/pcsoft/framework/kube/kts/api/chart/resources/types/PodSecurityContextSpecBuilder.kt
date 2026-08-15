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
     * Controls how the SELinux label of the pod's volumes is applied.
     *
     * Choosing [PodSecurityContextSpec.SELinuxChangePolicy.MountOption] avoids recursively relabelling
     * every file on a volume and is therefore considerably faster on large volumes, but it requires a
     * volume plugin that supports the corresponding mount option.
     */
    var seLinuxChangePolicy: PodSecurityContextSpec.SELinuxChangePolicy? = null

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
        seLinuxChangePolicy,
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
