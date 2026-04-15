package org.pcsoft.framework.kube.kts.api

@Target(AnnotationTarget.CLASS)
annotation class ResourceSpecHeader(val apiVersion: String, val kind: String)
