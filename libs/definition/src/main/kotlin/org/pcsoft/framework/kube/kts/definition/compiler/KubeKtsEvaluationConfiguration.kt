package org.pcsoft.framework.kube.kts.definition.compiler

import org.pcsoft.framework.kube.kts.api.values.ValueAccess
import tools.jackson.dataformat.yaml.YAMLMapper
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.scriptsInstancesSharing

class KubeKtsEvaluationConfiguration(private val valueAccess: ValueAccess) : ScriptEvaluationConfiguration({
    scriptsInstancesSharing(true)
    this.implicitReceivers(valueAccess)
}) {
    @Suppress("unused")
    constructor() : this(ValueAccess(YAMLMapper().createObjectNode()))
}
