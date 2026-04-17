package org.pcsoft.framework.kube.kts.core

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import java.io.File

class KubeKtsCompilerTest {
    companion object {
        private val compiler = KubeKtsCompiler()
    }

    @Test
    fun test() {
        val script = IOUtils.resourceToString("/helm/chart.kts", Charsets.UTF_8)
        compiler.compileAndExecute(script)
    }

}