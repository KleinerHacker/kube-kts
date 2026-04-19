package org.pcsoft.framework.kube.kts.definition.compiler

import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.scriptsInstancesSharing

object KubeKtsEvaluationConfiguration : ScriptEvaluationConfiguration({
    scriptsInstancesSharing(true)
})
