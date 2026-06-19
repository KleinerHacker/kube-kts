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

package org.pcsoft.framework.kube.kts.core.intern.assertions

import org.junit.jupiter.api.Assertions
import org.pcsoft.framework.kube.kts.api.chart.resources.SecretSpec
import org.pcsoft.framework.kube.kts.api.chart.template.FlatTemplateSpec

object SecretAssertion {

    fun assertMax(secretSpec: FlatTemplateSpec<SecretSpec>) {
        Assertions.assertNotNull(secretSpec.metadata)
        Assertions.assertEquals("secret", secretSpec.metadata.name)
        Assertions.assertEquals("namespace", secretSpec.metadata.namespace)

        Assertions.assertNotNull(secretSpec.spec)
        Assertions.assertEquals(SecretSpec.Type.Opaque, secretSpec.spec.type)

        Assertions.assertNotNull(secretSpec.spec.data)
        Assertions.assertArrayEquals("test".toByteArray(), secretSpec.spec.data!!["binKey"])

        Assertions.assertEquals(mapOf("username" to "admin"), secretSpec.spec.stringData)
        Assertions.assertEquals(true, secretSpec.spec.immutable)
    }
}
