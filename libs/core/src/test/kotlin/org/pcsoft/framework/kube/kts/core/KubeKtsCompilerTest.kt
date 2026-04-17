package org.pcsoft.framework.kube.kts.core

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.chart.ChartSpec
import org.pcsoft.framework.kube.kts.core.compiler.KubeKtsCompiler

class KubeKtsCompilerTest {
    companion object {
        private val compiler = KubeKtsCompiler()
    }

    @Test
    fun test() {
        val script = IOUtils.resourceToString("/helm/chart.kts", Charsets.UTF_8)

        val compiledScript = compiler.compile(StringKubeKzsFile(script, KubeKtsFile.Type.CHART))
        Assertions.assertNotNull(compiledScript)

        val chartSpec = compiler.execute<ChartSpec>(compiledScript)
        Assertions.assertNotNull(chartSpec)

        Assertions.assertEquals("demo", chartSpec.name)
        Assertions.assertEquals("1.0.0", chartSpec.version)
    }

}