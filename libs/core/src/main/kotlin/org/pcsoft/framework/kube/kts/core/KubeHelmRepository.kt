package org.pcsoft.framework.kube.kts.core

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