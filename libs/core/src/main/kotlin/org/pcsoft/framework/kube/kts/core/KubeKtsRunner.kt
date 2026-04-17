package org.pcsoft.framework.kube.kts.core

class KubeKtsRunner {
    private val compiler = KubeKtsCompiler()

    fun run() {
        println("Run script")
        compiler.compileAndExecute("5")
    }
}