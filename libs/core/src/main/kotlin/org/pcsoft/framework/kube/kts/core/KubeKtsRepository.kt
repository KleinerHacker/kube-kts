package org.pcsoft.framework.kube.kts.core

class KubeKtsRepository internal constructor(
    override val name: String,
    override val files: List<KubeKtsFile>,
    override val legacyFiles: List<LegacyHelmFile>
) :
    KubeRepository<KubeKtsFile> {
    override fun toString(): String {
        return name
    }
}