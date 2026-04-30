package org.pcsoft.framework.kube.kts.core

/**
 * A repository containing [KubeHelmFile]s.
 *
 * This class represents a collection of processed Helm-based Kubernetes files, alongside any
 * legacy Helm files. It provides an organized structure to manage these files for deployment
 * or rendering purposes. The repository is immutable and is constructed internally, ensuring
 * encapsulation of its state.
 *
 * The [name] represents the identifier of the repository. The [files] list contains the
 * evaluated Helm files ([KubeHelmFile]s), which encapsulate metadata and specification details.
 * The [legacyFiles] list holds legacy Helm templates ([LegacyHelmFile]s) that are used
 * for backward compatibility or alternative workflows.
 *
 * This repository is useful for scenarios where both modern Helm file formats and legacy
 * Helm templates must be managed together, such as during migrations or compatibility checks.
 */
class KubeHelmRepository internal constructor(
    override val name: String,
    override val files: List<KubeHelmFile>,
    override val legacyFiles: List<LegacyHelmFile>
) :
    KubeRepository<KubeHelmFile> {
    override fun toString(): String {
        return name
    }
}