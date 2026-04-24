package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.resources.types.*
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpecBuilder

class IngressSpecBuilder internal constructor() : ResourceSpecBuilder<IngressSpec> {
    private var defaultBackend: BackendSpecBuilder? = null
    private var tls: MutableList<TlsSpecBuilder>? = null
    private var rules: MutableList<RulesSpecBuilder>? = null

    var ingressClassName: String? = null

    fun defaultServiceBackend(name: String, prepare: ServiceBackendSpecBuilder.() -> Unit) {
        defaultBackend = ServiceBackendSpecBuilder(name).apply(prepare)
    }

    fun defaultResourceBackend(name: String, kind: String, prepare: ResourceBackendSpecBuilder.() -> Unit) {
        defaultBackend = ResourceBackendSpecBuilder(name, kind).apply(prepare)
    }

    fun addTls(prepare: TlsSpecBuilder.() -> Unit) {
        if (tls == null) {
            tls = mutableListOf()
        }

        tls!!.add(TlsSpecBuilder().apply(prepare))
    }

    fun addRule(prepare: RulesSpecBuilder.() -> Unit) {
        if (rules == null) {
            rules = mutableListOf()
        }

        rules!!.add(RulesSpecBuilder().apply(prepare))
    }

    override fun build(): IngressSpec = IngressSpec(
        ingressClassName,
        defaultBackend?.build(),
        tls?.map { it.build() },
        rules?.map { it.build() }
    )
}

fun ingress(prepare: TemplateSpecBuilder<IngressSpec, IngressSpecBuilder>.() -> Unit): TemplateSpec<IngressSpec> =
    TemplateSpecBuilder(IngressSpec.API_VERSION, IngressSpec.KIND, IngressSpecBuilder())
        .apply(prepare)
        .build()