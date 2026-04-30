package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.resources.types.BackendSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.RulesSpec
import org.pcsoft.framework.kube.kts.api.chart.resources.types.TlsSpec
import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents the specification for a Kubernetes Ingress.
 *
 * @property ingressClassName The name of the IngressClass cluster resource that this Ingress should be associated with.
 * @property defaultBackend The default backend for all requests that do not match any rule.
 * @property tls The TLS configuration for the Ingress.
 * @property rules The list of host rules used to configure the Ingress.
 */
@NoArgs
data class IngressSpec(
    val ingressClassName: String?,
    val defaultBackend: BackendSpec?,
    val tls: List<TlsSpec>?,
    val rules: List<RulesSpec>?
) : ResourceSpec {

    companion object {
        /**
         * Represents the kind of Kubernetes resource associated with this specification.
         * This constant identifies the resource as an Ingress.
         */
        const val KIND = "Ingress"

        /**
         * Defines the API version used for Kubernetes Ingress resources.
         *
         * This constant specifies the API group and version under which the Ingress
         * resource is defined in Kubernetes. It is typically used in the resource's
         * metadata to indicate the version of the Kubernetes API being used.
         *
         * Value: "networking.k8s.io/v1"
         */
        const val API_VERSION = "networking.k8s.io/v1"
    }

}