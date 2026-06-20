/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

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
        Assertions.assertEquals(9, ktsRepo.specFiles.size)
        Assertions.assertEquals(1, ktsRepo.specFiles.filter { it.isChart }.size)
        Assertions.assertTrue { ktsRepo.specFiles.any { it.subject == "Chart" } }
        Assertions.assertTrue { ktsRepo.specFiles.any { it.subject == "service" } }
        Assertions.assertTrue { ktsRepo.specFiles.any { it.subject == "ingress" } }
        Assertions.assertTrue { ktsRepo.specFiles.any { it.subject == "route" } }
        Assertions.assertTrue { ktsRepo.specFiles.any { it.subject == "job" } }
        Assertions.assertTrue { ktsRepo.specFiles.any { it.subject == "deployment" } }
        Assertions.assertTrue { ktsRepo.specFiles.any { it.subject == "configmap" } }
        Assertions.assertTrue { ktsRepo.specFiles.any { it.subject == "secret" } }
        Assertions.assertTrue { ktsRepo.specFiles.any { it.subject == "sealedsecret" } }
        Assertions.assertEquals(1, ktsRepo.libFiles.size)
        Assertions.assertTrue { ktsRepo.libFiles.any { it.subject == "helpers" } }
        Assertions.assertEquals(1, ktsRepo.legacyFiles.size)
        Assertions.assertEquals(1, ktsRepo.legacyFiles.filter { it.isValues }.size)

        val helmRepo = KubeKtsRepositoryBuilder.createDefault().build(ktsRepo, arrayOf())
        Assertions.assertNotNull(helmRepo)
        Assertions.assertEquals(9, helmRepo.specFiles.size)
        Assertions.assertEquals(1, helmRepo.specFiles.filter { it.isChart }.size)
        Assertions.assertEquals(1, helmRepo.legacyFiles.size)
        Assertions.assertEquals(1, helmRepo.legacyFiles.filter { it.isValues }.size)

        Assertions.assertTrue { helmRepo.specFiles.any { it.subject == "Chart" } }
        Assertions.assertTrue { helmRepo.specFiles.any { it.subject == "service" } }
        Assertions.assertTrue { helmRepo.specFiles.any { it.subject == "ingress" } }
        Assertions.assertTrue { helmRepo.specFiles.any { it.subject == "route" } }
        Assertions.assertTrue { helmRepo.specFiles.any { it.subject == "job" } }
        Assertions.assertTrue { helmRepo.specFiles.any { it.subject == "deployment" } }
        Assertions.assertTrue { helmRepo.specFiles.any { it.subject == "configmap" } }
        Assertions.assertTrue { helmRepo.specFiles.any { it.subject == "secret" } }
        Assertions.assertTrue { helmRepo.specFiles.any { it.subject == "sealedsecret" } }
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
            targetPath.resolve("templates/route.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/templates/route.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("templates/job.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/templates/job.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("templates/deployment.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/templates/deployment.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("templates/configmap.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/templates/configmap.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("templates/secret.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/templates/secret.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("templates/sealedsecret.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/templates/sealedsecret.yaml").toURI())
        )
        assertYaml(
            targetPath.resolve("values.yaml"),
            Paths.get(this::class.java.getResource("/kts/expected/values.yaml").toURI())
        )
    }

}
