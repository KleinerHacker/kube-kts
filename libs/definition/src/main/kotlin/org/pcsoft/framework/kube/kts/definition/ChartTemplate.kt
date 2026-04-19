package org.pcsoft.framework.kube.kts.definition

import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsCompilationConfiguration
import org.pcsoft.framework.kube.kts.definition.compiler.KubeKtsEvaluationConfiguration
import kotlin.script.experimental.annotations.KotlinScript

@Suppress("unused")
@KotlinScript(
    compilationConfiguration = KubeKtsCompilationConfiguration::class,
    evaluationConfiguration = KubeKtsEvaluationConfiguration::class,
    displayName = "Chart Template",
    filePathPattern = ".*[/\\\\]helm[/\\\\].*\\.kts"
)
abstract class ChartTemplate