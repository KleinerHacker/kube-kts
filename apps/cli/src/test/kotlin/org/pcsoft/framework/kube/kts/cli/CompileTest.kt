package org.pcsoft.framework.kube.kts.cli

import org.junit.jupiter.api.Test

class CompileTest {

    @Test
    fun test() {
        main(arrayOf("compile", "src/test/resources/helm"))
    }

}