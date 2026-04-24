package org.pcsoft.framework.kube.kts.core

interface KubeRepository<T> {
    val name: String
    val files: List<T>
}