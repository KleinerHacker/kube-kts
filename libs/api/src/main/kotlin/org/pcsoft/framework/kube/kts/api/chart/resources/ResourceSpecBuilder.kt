package org.pcsoft.framework.kube.kts.api.chart.resources

/**
 * Represents a builder interface for creating instances of specific Kubernetes resource specifications.
 *
 * Implementations of this interface are responsible for providing customized
 * configurations for Kubernetes resources by building instances of the specified
 * type that extends `ResourceSpec`.
 *
 * @param S The type of the resource specification that this builder creates. It must
 *          extend the `ResourceSpec` interface.
 */
interface ResourceSpecBuilder<S : ResourceSpec> {
    /**
     * Builds and returns an instance of the resource specification.
     *
     * @return An instance of type S representing the resource specification.
     */
    fun build(): S
}