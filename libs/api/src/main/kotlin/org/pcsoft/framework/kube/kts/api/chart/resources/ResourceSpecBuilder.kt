package org.pcsoft.framework.kube.kts.api.chart.resources

interface ResourceSpecBuilder<S : ResourceSpec> {
    fun build(): S
}