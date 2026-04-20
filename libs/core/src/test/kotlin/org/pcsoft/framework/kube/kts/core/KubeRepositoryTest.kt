package org.pcsoft.framework.kube.kts.core

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import java.nio.file.Paths

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class KubeRepositoryTest {

    @Test
    fun testSuccessfully() {
        val ktsRepo = KubeKtsRepositoryScanner.DEFAULT.scan(Paths.get(this::class.java.getResource("/helm").toURI()))
        Assertions.assertNotNull(ktsRepo)
        Assertions.assertEquals(2, ktsRepo.files.size)
        Assertions.assertEquals(1, ktsRepo.files.filter { it.type == KubeFile.Type.CHART }.size)
        Assertions.assertEquals(1, ktsRepo.files.filter { it.type == KubeFile.Type.TEMPLATE }.size)
        Assertions.assertTrue { ktsRepo.files.any { it.subject == "chart" } }
        Assertions.assertTrue { ktsRepo.files.any { it.subject == "service" } }

        val helmRepo = KubeKtsRepositoryBuilder.DEFAULT.build(ktsRepo)
        Assertions.assertNotNull(helmRepo)
        Assertions.assertEquals(2, helmRepo.files.size)
        Assertions.assertEquals(1, helmRepo.files.filter { it.type == KubeFile.Type.CHART }.size)
        Assertions.assertEquals(1, helmRepo.files.filter { it.type == KubeFile.Type.TEMPLATE }.size)
        Assertions.assertTrue { helmRepo.files.any { it.subject == "chart" } }
        Assertions.assertTrue { helmRepo.files.any { it.subject == "service" } }
    }

}