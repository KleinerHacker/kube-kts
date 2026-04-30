package org.pcsoft.framework.kube.kts.core

/**
 * Represents a repository containing Kubernetes Kotlin Script (KTS) files.
 *
 * This class implements the [KubeRepository] interface to encapsulate
 * a collection of Kubernetes-related Kotlin Script files ([KubeKtsFile])
 * along with any associated legacy Helm files ([LegacyHelmFile]).
 *
 * It is designed to provide a structured way to manage and interact
 * with repositories comprising modern Kotlin Script-based definitions
 * and legacy YAML templates for Kubernetes resources.
 *
 * @constructor Internal constructor used for instantiating the repository.
 *
 * @property name The name of the repository.
 * @property files The list of processed Kubernetes Kotlin Script files in the repository.
 * @property legacyFiles The list of legacy Helm files (YAML templates) in the repository.
 */
class KubeKtsRepository internal constructor(
    override val name: String,
    override val files: List<KubeKtsFile>,
    override val legacyFiles: List<LegacyHelmFile>
) : KubeRepository<KubeKtsFile> {

    /**
     * Returns the string representation of the repository.
     *
     * @return The name of the repository.
     */
    override fun toString(): String {
        return name
    }
}