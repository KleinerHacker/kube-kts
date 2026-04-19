package org.pcsoft.framework.kube.kts.core.compiler

import org.pcsoft.framework.kube.kts.core.CompiledKubeKtsFile
import org.pcsoft.framework.kube.kts.core.PlainKubeKtsFile

interface KubeKtsCompiler {
    fun compile(kubeFile: PlainKubeKtsFile) : CompiledKubeKtsFile
    fun <T>execute(file: CompiledKubeKtsFile) : T
}