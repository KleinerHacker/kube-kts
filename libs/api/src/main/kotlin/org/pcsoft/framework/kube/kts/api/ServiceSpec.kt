package org.pcsoft.framework.kube.kts.api

@ResourceSpecHeader("v1", "Service")
class ServiceSpec(metadata: ResourceSpecMetadata) : ResourceSpec<ResourceSpecMetadata>(metadata) {

}

class ServiceSpecBuilder {
    private lateinit var specMetadata: ResourceSpecMetadata

    fun metadata(prepare: DefaultResourceSpecMetadataBuilder.() -> Unit) : ResourceSpecMetadata {
        specMetadata = DefaultResourceSpecMetadataBuilder().apply(prepare).build()
        return specMetadata
    }

    internal fun build(): ServiceSpec {
        require(::specMetadata.isInitialized) { "Metadata is required" }

        return ServiceSpec(specMetadata)
    }
}

fun serviceSpec(prepare: ServiceSpecBuilder.() -> Unit) : ServiceSpec =
    ServiceSpecBuilder().apply(prepare).build()