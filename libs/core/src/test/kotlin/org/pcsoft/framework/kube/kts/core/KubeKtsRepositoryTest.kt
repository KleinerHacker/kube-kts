package org.pcsoft.framework.kube.kts.core

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.core.compiler.DefaultKubeKtsCompiler
import java.nio.file.Paths

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class KubeKtsRepositoryTest {

    @Test
    fun testSuccessfully() {
        val repo = KubeKtsRepository(Paths.get(this::class.java.getResource("/helm").toURI()))
        Assertions.assertEquals(KubeKtsRepository.State.INITIALIZED, repo.state)
        repo.scan()
        Assertions.assertEquals(KubeKtsRepository.State.SCANNED, repo.state)
        repo.compile(DefaultKubeKtsCompiler::compile)
        Assertions.assertEquals(KubeKtsRepository.State.COMPILED, repo.state)
    }

}