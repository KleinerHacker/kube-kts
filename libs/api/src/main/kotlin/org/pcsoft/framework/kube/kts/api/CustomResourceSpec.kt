package org.pcsoft.framework.kube.kts.api

import org.pcsoft.framework.kube.kts.api.types.MetadataSpec

@Suppress("unused")
abstract class CustomResourceSpec<M : MetadataSpec>(metadata: M) : ResourceSpec<M>(metadata) {
}