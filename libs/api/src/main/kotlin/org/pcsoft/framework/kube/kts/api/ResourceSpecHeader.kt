package org.pcsoft.framework.kube.kts.api

@Target(AnnotationTarget.CLASS)
annotation class ResourceSpecHeader(val apiVersion: String, val kind: String)

data class ResourceSpecHeaderWrapper(val apiVersion: String, val kind: String) {
    constructor(header: ResourceSpecHeader) : this(header.apiVersion, header.kind)
}
