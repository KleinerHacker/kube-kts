package org.pcsoft.framework.kube.kts.core

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.core.builder.KubeKtsRepositoryBuilder
import org.pcsoft.framework.kube.kts.core.renderer.KubeHelmRepositoryRenderer
import org.pcsoft.framework.kube.kts.core.scanner.KubeKtsRepositoryScanner
import java.nio.file.Files
import java.nio.file.Paths

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class KubeRepositoryTest : RepositoryTest() {

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testSuccessfully() {
        val ktsRepo =
            KubeKtsRepositoryScanner.DEFAULT.scan(Paths.get(this::class.java.getResource("/kts/helm").toURI()))
        Assertions.assertNotNull(ktsRepo)
        Assertions.assertEquals(3, ktsRepo.files.size)
        Assertions.assertEquals(1, ktsRepo.files.filter { it.isChart }.size)
        Assertions.assertTrue { ktsRepo.files.any { it.subject == "Chart" } }
        Assertions.assertTrue { ktsRepo.files.any { it.subject == "service" } }
        Assertions.assertTrue { ktsRepo.files.any { it.subject == "ingress" } }
        Assertions.assertEquals(1, ktsRepo.legacyFiles.size)
        Assertions.assertEquals(1, ktsRepo.legacyFiles.filter { it.isValues }.size)

        val helmRepo = KubeKtsRepositoryBuilder.createDefault().build(ktsRepo, arrayOf())
        Assertions.assertNotNull(helmRepo)
        Assertions.assertEquals(3, helmRepo.files.size)
        Assertions.assertEquals(1, helmRepo.files.filter { it.isChart }.size)
        Assertions.assertEquals(1, helmRepo.legacyFiles.size)
        Assertions.assertEquals(1, helmRepo.legacyFiles.filter { it.isValues }.size)

        Assertions.assertTrue { helmRepo.files.any { it.subject == "Chart" } }
        Assertions.assertTrue { helmRepo.files.any { it.subject == "service" } }
        Assertions.assertTrue { helmRepo.files.any { it.subject == "ingress" } }
        Assertions.assertTrue { helmRepo.legacyFiles.any { it.subject == "values" } }

        val targetPath = Files.createTempDirectory("helm")
        KubeHelmRepositoryRenderer.DEFAULT.render(helmRepo, targetPath)
        assertYaml(
            targetPath.resolve("Chart.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/Chart.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("templates/service.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/templates/service.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("templates/ingress.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/templates/ingress.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("values.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/values.yaml").toURI())
        )
    }

}