package org.pcsoft.framework.kube.kts.api.chart.resources.types

class TlsSpecBuilder internal constructor() {
    private var hosts: MutableList<String>? = null

    var secretName: String? = null

    fun addHost(host: String): TlsSpecBuilder {
        if (hosts == null) {
            hosts = mutableListOf()
        }
        hosts!!.add(host)
        return this
    }

    internal fun build(): TlsSpec = TlsSpec(hosts, secretName)
}