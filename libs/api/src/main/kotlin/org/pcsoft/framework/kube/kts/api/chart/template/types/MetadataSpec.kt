package org.pcsoft.framework.kube.kts.api.chart.template.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

@NoArgs
data class MetadataSpec(val name: String, val generateName: String?, val namespace: String?)