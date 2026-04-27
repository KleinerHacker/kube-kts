package org.pcsoft.framework.kube.kts.core

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.intern.setupTestLogger
import org.pcsoft.framework.kube.kts.core.renderer.KubeHelmRepositoryRenderer
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import java.nio.file.Files
import java.nio.file.Paths

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class KubeMixRepositoryTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            setupTestLogger()
        }
    }


    @Suppress("UNCHECKED_CAST")
    @Test
    fun testSuccessfully() {
        val ktsRepo = KubeKtsRepositoryScanner.DEFAULT.scan(Paths.get(this::class.java.getResource("/mix/helm").toURI()))
        Assertions.assertNotNull(ktsRepo)
        Assertions.assertEquals(2, ktsRepo.files.size)
        Assertions.assertEquals(1, ktsRepo.files.filter { it.isChart }.size)
        Assertions.assertTrue { ktsRepo.files.any { it.subject == "Chart" } }
        Assertions.assertTrue { ktsRepo.files.any { it.subject == "service" } }
        Assertions.assertEquals(2, ktsRepo.legacyFiles.size)
        Assertions.assertEquals(0, ktsRepo.legacyFiles.filter { it.isChart }.size)
        Assertions.assertTrue { ktsRepo.legacyFiles.any { it.subject == "ingress" } }
        Assertions.assertTrue { ktsRepo.legacyFiles.any { it.subject == "helper" } }

        val helmRepo = KubeKtsRepositoryBuilder.DEFAULT.build(ktsRepo, arrayOf())
        Assertions.assertNotNull(helmRepo)
        Assertions.assertEquals(2, helmRepo.files.size)
        Assertions.assertEquals(1, helmRepo.files.filter { it.isChart }.size)
        Assertions.assertEquals(2, helmRepo.legacyFiles.size)
        Assertions.assertEquals(0, helmRepo.legacyFiles.filter { it.isChart }.size)

        Assertions.assertTrue { helmRepo.files.any { it.subject == "Chart" } }
        Assertions.assertTrue { helmRepo.files.any { it.subject == "service" } }
        Assertions.assertTrue { helmRepo.legacyFiles.any { it.subject == "ingress" } }
        Assertions.assertTrue { helmRepo.legacyFiles.any { it.subject == "helper" } }

        val targetPath = Files.createTempDirectory("helm")
        KubeHelmRepositoryRenderer.DEFAULT.render(helmRepo, targetPath)
        Assertions.assertTrue { Files.exists(targetPath.resolve("Chart.yaml")) }
        Assertions.assertTrue { Files.exists(targetPath.resolve("templates/service.yaml")) }
        Assertions.assertTrue { Files.exists(targetPath.resolve("templates/ingress.yaml")) }
        Assertions.assertTrue { Files.exists(targetPath.resolve("templates/helper/helper.tpl")) }
    }

}