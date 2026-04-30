package org.pcsoft.framework.kube.kts.core

import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import java.nio.file.Path

/**
 * Represents a processed Helm file within the Kubernetes ecosystem.
 *
 * This interface extends the base [KubeFile] to include an associated
 * specification object, which is the result of evaluating the Helm file.
 * It is designed to encapsulate both the file's metadata and its evaluated
 * specification details, making it suitable for rendering or deployment processes.
 *
 * Subtypes or implementations of this interface may be created to handle
 * specific use cases or processing needs.
 */
interface KubeHelmFile : KubeFile {
    /**
     * The specification object resulting from evaluating the file.
     */
    val spec: KubeSpec
}

/**
 * Represents the default implementation of a processed Helm file within the Kubernetes ecosystem.
 *
 * This class encapsulates metadata and evaluated specification details for a Helm file.
 * It is constructed using either individual properties or by converting a given [KubeKtsFile].
 *
 * @property subject The subject of the file. Typically refers to the type or purpose of the file
 * (e.g., "chart", "values", etc.).
 * @property relativePath The path of the file relative to the root of the repository.
 * @property isChart Indicates if this file represents a Helm chart definition.
 * @property spec The specification object resulting from the evaluation of the Helm file.
 */
internal data class DefaultKubeHelmFile(
    override val subject: String,
    override val relativePath: Path,
    override val isChart: Boolean,
    override val spec: KubeSpec
) : KubeHelmFile {

    constructor(file: KubeKtsFile, spec: KubeSpec) : this(
        file.subject, file.relativePath, file.isChart, spec
    )

}