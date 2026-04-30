package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.KubeSpec

/**
 * Represents a generic marker interface for Kubernetes resource specifications.
 *
 * Classes implementing this interface define the structure and configuration
 * of various Kubernetes resources, serving as a contract for resource-related
 * specifications in the domain of Kubernetes API integration.
 *
 * This interface extends the base `KubeSpec` interface, enabling its implementations
 * to act as Kubernetes resource specifications, such as those for ingress, service,
 * pod, or deployment configurations.
 */
interface ResourceSpec : KubeSpec