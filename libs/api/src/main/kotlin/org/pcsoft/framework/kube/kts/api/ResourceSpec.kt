package org.pcsoft.framework.kube.kts.api

interface ResourceSpec

interface ResourceSpecBuilder<S : ResourceSpec> {
    fun build(): S
}