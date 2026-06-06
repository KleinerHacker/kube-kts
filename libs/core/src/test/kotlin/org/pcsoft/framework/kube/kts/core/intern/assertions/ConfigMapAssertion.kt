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
import org.pcsoft.framework.kube.kts.api.chart.resources.ConfigMapSpec
import org.pcsoft.framework.kube.kts.api.chart.template.TemplateSpec

object ConfigMapAssertion {

    fun assertMax(configMapSpec: TemplateSpec<ConfigMapSpec>) {
        Assertions.assertNotNull(configMapSpec.metadata)
        Assertions.assertEquals("configmap", configMapSpec.metadata.name)
        Assertions.assertEquals("namespace", configMapSpec.metadata.namespace)

        Assertions.assertNotNull(configMapSpec.spec)
        Assertions.assertEquals(mapOf("key1" to "value1", "key2" to "value2"), configMapSpec.spec.data)

        Assertions.assertNotNull(configMapSpec.spec.binaryData)
        Assertions.assertArrayEquals("test".toByteArray(), configMapSpec.spec.binaryData!!["binKey"])

        Assertions.assertEquals(true, configMapSpec.spec.immutable)
    }
}
