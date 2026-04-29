package org.pcsoft.framework.kube.kts.core

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.renderer.KubeHelmRepositoryRenderer
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import java.nio.file.Files
import java.nio.file.Paths

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class KubeMixRepositoryTest : RepositoryTest() {

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testSuccessfully() {
        val ktsRepo = KubeKtsRepositoryScanner.DEFAULT.scan(Paths.get(this::class.java.getResource("/mix/helm").toURI()))
        Assertions.assertNotNull(ktsRepo)
        Assertions.assertEquals(2, ktsRepo.files.size)
        Assertions.assertEquals(1, ktsRepo.files.filter { it.isChart }.size)
        Assertions.assertTrue { ktsRepo.files.any { it.subject == "Chart" } }
        Assertions.assertTrue { ktsRepo.files.any { it.subject == "service" } }
        Assertions.assertEquals(3, ktsRepo.legacyFiles.size)
        Assertions.assertEquals(0, ktsRepo.legacyFiles.filter { it.isChart }.size)
        Assertions.assertEquals(1, ktsRepo.legacyFiles.filter { it.isValues }.size)
        Assertions.assertTrue { ktsRepo.legacyFiles.any { it.subject == "ingress" } }
        Assertions.assertTrue { ktsRepo.legacyFiles.any { it.subject == "helper" } }
        Assertions.assertTrue { ktsRepo.legacyFiles.any { it.subject == "values" } }

        val helmRepo = KubeKtsRepositoryBuilder.createDefault().build(ktsRepo, arrayOf())
        Assertions.assertNotNull(helmRepo)
        Assertions.assertEquals(2, helmRepo.files.size)
        Assertions.assertEquals(1, helmRepo.files.filter { it.isChart }.size)
        Assertions.assertEquals(3, helmRepo.legacyFiles.size)
        Assertions.assertEquals(0, helmRepo.legacyFiles.filter { it.isChart }.size)
        Assertions.assertEquals(1, helmRepo.legacyFiles.filter { it.isValues }.size)

        Assertions.assertTrue { helmRepo.files.any { it.subject == "Chart" } }
        Assertions.assertTrue { helmRepo.files.any { it.subject == "service" } }
        Assertions.assertTrue { helmRepo.legacyFiles.any { it.subject == "ingress" } }
        Assertions.assertTrue { helmRepo.legacyFiles.any { it.subject == "helper" } }
        Assertions.assertTrue { helmRepo.legacyFiles.any { it.subject == "values" } }

        val targetPath = Files.createTempDirectory("helm")
        KubeHelmRepositoryRenderer.DEFAULT.render(helmRepo, targetPath)
        assertYaml(
            targetPath.resolve("Chart.yaml"),
            Paths.get(this::class.java.getResource("/mix/expected/Chart.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("templates/service.yaml"),
            Paths.get(this::class.java.getResource("/mix/expected/templates/service.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("templates/ingress.yaml"),
            Paths.get(this::class.java.getResource("/mix/expected/templates/ingress.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("values.yaml"),
            Paths.get(this::class.java.getResource("/mix/expected/values.yaml").toURI())
        )
        assertContent(
            targetPath.resolve("templates/helper/helper.tpl"),
            Paths.get(this::class.java.getResource("/mix/expected/templates/helper/helper.tpl").toURI())
        )
    }

}