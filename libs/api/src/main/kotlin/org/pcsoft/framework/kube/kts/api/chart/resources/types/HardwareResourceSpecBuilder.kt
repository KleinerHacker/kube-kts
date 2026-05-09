package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.types.CpuValue
import org.pcsoft.framework.kube.kts.api.types.MemoryValue

/**
 * Builder class for constructing hardware resource specifications including limits and requests.
 * 
 * This class is used to define the compute resource limits and requests for containerized applications.
 */
class HardwareResourceSpecBuilder internal constructor() {
    private var limits: ResourceDataBuilder? = null
    private var requests: ResourceDataBuilder? = null

    /**
     * Configures the resource limits for a containerized application.
     *
     * The specified `prepare` lambda allows users to define CPU, memory, ephemeral storage,
     * and extended resources for the container. These resource limits are used to enforce
     * restrictions on the amount of resources a container can consume.
     *
     * Example:
     * ```kotlin
     *     limits {
     *         cpu = CpuValue.of(2)
     *         memory = MemoryValue.ofGiB(4)
     *     }
     * ```
     *
     * @param prepare A lambda expression used to define the resource limits 
     *                by configuring properties of the `ResourceDataBuilder`.
     */
    fun limits(prepare: ResourceDataBuilder.() -> Unit) {
        limits = ResourceDataBuilder().apply(prepare)
    }

    /**
     * Configures the resource requests for a containerized application.
     *
     * The specified `prepare` lambda allows users to define CPU, memory, 
     * ephemeral storage, and extended resources for the container. These 
     * resource requests indicate the minimum amount of resources a container 
     * is expected to consume.
     *
     * Example:
     * ```kotlin
     *     requests {
     *         cpu = CpuValue.of(1)
     *         memory = MemoryValue.ofGiB(2)
     *     }
     * ```
     *
     * @param prepare A lambda expression used to define the resource requests
     *                by configuring properties of the `ResourceDataBuilder`.
     */
    fun requests(prepare: ResourceDataBuilder.() -> Unit) {
        requests = ResourceDataBuilder().apply(prepare)
    }

    /**
     * Builds and returns a `ResourceSpec` object based on the current configuration of resource limits and requests.
     * 
     * This method consolidates the configured `limits` and `requests` resource data into a `ResourceSpec` instance.
     * It is used to define the minimum and maximum resource requirements for a container.
     *
     * @return A `ResourceSpec` object containing resource limits and requests, or null if unconfigured.
     */
    internal fun build(): HardwareResourceSpec {
        val limits = this.limits?.build()
        val requests = this.requests?.build()

        if (limits != null && requests != null) {
            if (limits.cpu != null && requests.cpu != null) {
                require(limits.cpu >= requests.cpu) { "CPU limits must be greater or equals to requested value" }
            }
            if (limits.memory != null && requests.memory != null) {
                require(limits.memory >= requests.memory) { "Memory limits must be greater or equals to requested value" }
            }
            if (limits.ephemeralStorage != null && requests.ephemeralStorage != null) {
                require(limits.ephemeralStorage >= requests.ephemeralStorage) { "Ephemeral storage limits must be greater or equals to requested value" }
            }
        }

        return HardwareResourceSpec(requests, limits)
    }

    /**
     * Builder class for configuring resource data for a containerized application.
     *
     * `ResourceDataBuilder` allows users to define resource requirements or limits,
     * including CPU, memory, ephemeral storage, and extended resources, which are
     * key for managing container resource allocation and usage.
     *
     * @constructor Internal constructor for creating a new instance of `ResourceDataBuilder`.
     *              Instances of this class should be created only within the
     *              containing scope.
     */
    class ResourceDataBuilder internal constructor() {
        private var extendedResources: MutableMap<String, String>? = null

        /**
         * Represents the CPU resource allocation or limit for a container in the resource configuration.
         *
         * This variable is used to define or constrain the computational resources allocated
         * for a containerized application running within a pod. The value is optional and can
         * either be null (indicating no specific CPU allocation or limit is set) or an instance
         * of [CpuValue], which specifies the CPU value in fractional or full CPU units.
         *
         * The CPU resource is essential for performance management and fine-tuning the workload
         * behavior in containerized environments.
         */
        var cpu: CpuValue? = null

        /**
         * The `memory` variable represents the memory capacity or allocation for the current resource.
         * 
         * It is an instance of the `MemoryValue` class, which encapsulates a value in bytes 
         * with support for binary storage units (KiB, MiB, GiB). The value can include operations 
         * like conversion between units, arithmetic manipulations, and serialization to YAML-friendly 
         * formats. 
         * 
         * This variable may be nullable, indicating that the memory resource might not be explicitly set.
         */
        var memory: MemoryValue? = null

        /**
         * Defines an optional ephemeral storage allocation for a resource.
         *
         * Ephemeral storage refers to temporary local storage available to the resource.
         * It typically includes space for caches, logs, or any other non-persistent data that does not
         * need to be preserved beyond the resource's lifecycle. 
         *
         * Setting this variable allows configuration of the desired ephemeral storage size,
         * represented using the `MemoryValue` class, which supports binary units like KiB, MiB, or GiB.
         *
         * A value of `null` indicates that no specific ephemeral storage requirement is set.
         */
        var ephemeralStorage: MemoryValue? = null

        /**
         * Adds an extended resource to the current resource configuration.
         *
         * This method allows specifying custom resource requirements beyond the standard CPU and memory limitations.
         * Extended resources are typically used for defining requirements for devices such as GPUs or custom hardware.
         *
         * @param key The name of the extended resource. This should follow the Kubernetes extended resource naming convention.
         * @param value The quantity of the extended resource being requested, represented as a string.
         */
        fun addExtendedResource(key: String, value: String) {
            if (extendedResources == null) {
                extendedResources = mutableMapOf()
            }
            extendedResources!![key] = value
        }

        /**
         * Configures extended resource requirements for a resource.
         *
         * This method allows customization of additional resource requirements beyond standard resource limits
         * such as CPU and memory. It provides a DSL for specifying extended resources, which are often used for
         * devices like GPUs or other custom hardware components.
         *
         * Example:
         * ```kotlin
         *     extendedResources {
         *         extendedResource("nvidia.com/gpu", "2")
         *         extendedResource("example.com/custom-device", "1")
         *     }
         * ```
         *
         * @param prepare A lambda block used to define extended resources. The block operates on an instance 
         *                of `ExtendedResourcesBuilder`, providing methods to define specific extended resource needs.
         */
        fun extendedResources(prepare: ExtendedResourcesBuilder.() -> Unit) {
            ExtendedResourcesBuilder().apply(prepare)
        }

        /**
         * Builds and returns a `ResourceSpec.Data` object based on the current resource configuration.
         *
         * This method consolidates the configured values for CPU, memory, ephemeral storage, 
         * and extended resources into a `ResourceSpec.Data` instance, which is used to define 
         * the resource requirements or limits for a container or workload.
         *
         * @return A `ResourceSpec.Data` object containing the configured resource requirements or limits.
         */
        internal fun build(): HardwareResourceSpec.Data = HardwareResourceSpec.Data(
            cpu = cpu,
            memory = memory,
            ephemeralStorage = ephemeralStorage,
            extendedResources = extendedResources
        )

        /**
         * Provides a builder for configuring extended resource requirements.
         * 
         * This class is designed to define additional resource requirements beyond the standard
         * CPU and memory configurations, such as GPUs or other custom hardware. It acts as a
         * sub-DSL for adding extended resource definitions to the parent resource configuration.
         *
         * @constructor Internal. Instances of this class should only be created within the scope
         * of the parent [ResourceDataBuilder].
         */
        inner class ExtendedResourcesBuilder internal constructor() {
            /**
             * Defines an extended resource requirement for the current resource configuration.
             *
             * This method is used to specify custom resource requirements, such as GPUs
             * or other specialized hardware. Extended resources are beyond the standard CPU 
             * and memory configurations.
             *
             * @param key The name of the extended resource. Must follow the Kubernetes extended resource naming convention.
             * @param value The quantity of the extended resource being requested, represented as a string.
             */
            fun extendedResource(key: String, value: String) {
                addExtendedResource(key, value)
            }       
        }
    }
}