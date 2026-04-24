package org.pcsoft.framework.kube.kts.core

interface KubeRepository<T : KubeFile> {
    val name: String
    val files: List<T>
    val legacyFiles: List<LegacyHelmFile>
}