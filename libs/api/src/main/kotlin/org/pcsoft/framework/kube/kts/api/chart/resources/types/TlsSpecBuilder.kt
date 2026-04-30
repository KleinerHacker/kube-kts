package org.pcsoft.framework.kube.kts.api.chart.resources.types

/**
 * Builder class for constructing a [TlsSpec] instance, which specifies the TLS configuration
 * for an Ingress, including hosts and the secret containing the TLS certificate and private key.
 *
 * This builder supports the dynamic configuration of hosts and secret names. It also provides
 * an internal nested [HostListBuilder] class for a more structured way to add multiple hosts.
 *
 * @constructor Internal constructor to initialize the builder.
 *              Instance creation is restricted to internal usage.
 */
class TlsSpecBuilder internal constructor() {
    private var hosts: MutableList<String>? = null

    /**
     * The name of the secret that contains the TLS certificate and private key.
     *
     * This is used to configure the TLS settings for an Ingress. The secret specified by this
     * name must be pre-created and should include the necessary certificate and key files.
     * It allows secure communication between the client and the server by enabling HTTPS.
     *
     * Set this property to specify which Kubernetes Secret resource should be used for TLS termination.
     */
    var secretName: String? = null

    /**
     * Adds a host to the list of TLS hosts to be included in the TLS specification.
     *
     * @param host The hostname to be added to the list of TLS hosts.
     * @return The current instance of [TlsSpecBuilder] to allow method chaining.
     */
    fun addHost(host: String): TlsSpecBuilder {
        if (hosts == null) {
            hosts = mutableListOf()
        }
        hosts!!.add(host)
        return this
    }

    /**
     * Configures a list of hosts for inclusion in the TLS specification by using the provided [HostListBuilder].
     *
     * This method allows dynamic and structured configuration of TLS hosts. The [prepare] lambda function
     * is used to configure the underlying [HostListBuilder] instance, enabling the addition of multiple
     * host entries in a concise and readable manner.
     *
     * Example usage:
     * ```kotlin
 *     hosts {
 *         host("example.com")
 *         host("www.example.com")
 *         host("api.example.com")
 *     }
     * ```
     *
     * @param prepare A lambda that operates on an instance of [HostListBuilder], allowing the host list
     *                to be dynamically configured by invoking the `host()` method for each desired hostname.
     */
    fun hosts(prepare: HostListBuilder.() -> Unit) {
        HostListBuilder().apply(prepare)
    }

    internal fun build(): TlsSpec = TlsSpec(hosts, secretName)

    /**
     * Builder for constructing a list of TLS hosts within the TLS specification configuration.
     *
     * This class provides a structured way to define and add individual hostnames to the TLS host list.
     * The [host] method allows the addition of a single hostname to the list, which will be included
     * in the final TLS specification.
     */
    inner class HostListBuilder internal constructor() {
        /**
         * Adds a hostname to the list of TLS hosts.
         *
         * This method appends the provided hostname to the collection of TLS hosts, which will be used
         * in the TLS configuration for the corresponding resource.
         *
         * @param host The hostname to be added to the TLS hosts list.
         */
        fun host(host: String) {
            addHost(host)
        }
    }
}