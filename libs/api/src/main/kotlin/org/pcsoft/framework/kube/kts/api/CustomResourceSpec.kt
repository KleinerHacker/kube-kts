package org.pcsoft.framework.kube.kts.api

abstract class CustomResourceSpec<M : ResourceSpecMetadata>(metadata: M) : ResourceSpec<M>(metadata) {
}