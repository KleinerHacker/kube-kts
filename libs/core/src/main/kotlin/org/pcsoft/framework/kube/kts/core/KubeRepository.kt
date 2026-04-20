package org.pcsoft.framework.kube.kts.core

interface KubeRepository<T> {
    val files: List<T>
}