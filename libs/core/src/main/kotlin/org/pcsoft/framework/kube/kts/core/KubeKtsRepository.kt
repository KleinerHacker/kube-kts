package org.pcsoft.framework.kube.kts.core

class KubeKtsRepository internal constructor(override val name: String, override val files: List<KubeKtsFile>) :
    KubeRepository<KubeKtsFile> {
    override fun toString(): String {
        return name
    }
}