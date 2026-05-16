/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CpuValueTest {

    @Test
    fun testYaml() {
        assertEquals("100m", 0.1f.cpu.toYamlValue())
        assertEquals("1000m", 1f.cpu.toYamlValue())
        assertEquals("10000m", 10f.cpu.toYamlValue())
    }

    @Test
    fun testParse() {
        assertEquals(0.1f, CpuValue.parse("100m").value)
        assertEquals(1f, CpuValue.parse("1000m").value)
        assertEquals(10f, CpuValue.parse("10000m").value)

        assertEquals(10f, CpuValue.parse("10").value)
    }

}