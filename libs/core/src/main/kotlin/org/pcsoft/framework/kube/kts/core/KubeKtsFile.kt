package org.pcsoft.framework.kube.kts.core

import java.nio.file.Path

/**
 * Represents a Kubernetes-related file containing Kotlin Script (KTS) content.
 *
 * This interface extends the base [KubeFile] to specifically handle Kotlin Script files
 * within the Kubernetes ecosystem. It adds the ability to retrieve the raw Kotlin Script
 * content, which can later be processed or evaluated within a specific context.
 *
 * Subtypes or implementations of this interface may provide additional functionality
 * specific to managing and interacting with Kotlin-based definitions for Kubernetes resources.
 */
interface KubeKtsFile : KubeFile {
    /**
     * The raw Kotlin Script content.
     */
    val script: String
}

/**
 * Represents the default implementation of the [KubeKtsFile] interface for Kubernetes Kotlin Script files.
 *
 * This class encapsulates metadata and content related to a Kubernetes Kotlin Script (KTS) file,
 * including its subject, relative path within the repository, and the raw script content.
 * It serves as a standard representation of a KTS file structure within the Kubernetes ecosystem.
 *
 * @property subject The subject of the file, typically describing its type or purpose (e.g., "chart", "values").
 * @property relativePath The path of the file relative to the root of the repository.
 * @property script The raw content of the Kotlin Script associated with this file.
 */
internal data class DefaultKubeKtsFile(
    override val subject: String,
    override val relativePath: Path,
    override val script: String
) : KubeKtsFile