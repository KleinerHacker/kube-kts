package org.pcsoft.framework.kube.kts.core

import java.nio.file.Path

/**
 * Represents a Kubernetes-related file within a repository.
 *
 * This interface is designed as the base abstraction for various types of Kubernetes files,
 * such as Helm chart files, values files, or other resource definitions. Subtypes of this
 * interface may include specific file representations (e.g., Kotlin Script files or processed
 * specification files).
 */
interface KubeFile {
    companion object {
        private const val CHART_SUBJECT = "chart"
        private const val VALUES_SUBJECT = "values"
    }

    /**
     * The subject of the file (e.g., "chart", "values", or a resource kind like "service").
     */
    val subject: String

    /**
     * The path of the file relative to the repository root.
     */
    val relativePath: Path

    /**
     * Returns true if this file represents a Helm chart definition.
     */
    val isChart: Boolean get() = subject.equals(CHART_SUBJECT, true)

    /**
     * Returns true if this file represents a values definition.
     */
    val isValues: Boolean get() = subject.equals(VALUES_SUBJECT, true)
}

/**
 * Represents a legacy Helm file (YAML template).
 *
 * @property subject The subject/name of the file.
 * @property relativePath The path relative to the repository root.
 * @property extension The file extension.
 * @property content The raw content of the file.
 */
data class LegacyHelmFile(
    override val subject: String,
    override val relativePath: Path,
    val extension: String,
    val content: String
) : KubeFile