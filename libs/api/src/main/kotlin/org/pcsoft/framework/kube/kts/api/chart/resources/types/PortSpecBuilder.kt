/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart.resources.types

/**
 * Builder class for creating instances of `PortSpec`.
 *
 * This builder allows for specifying a port either by its name or by its number,
 * but not both simultaneously.
 */
class PortSpecBuilder private constructor(private val name: String?, private val number: Int?) {
    /**
     * Secondary constructor for `PortSpecBuilder` that initializes
     * an instance based on the given port name without a port number.
     *
     * @param name The name of the port.
     */
    internal constructor(name: String) : this(name, null)

    /**
     * Secondary constructor for `PortSpecBuilder` that initializes an instance
     * based on the given port number without a port name.
     *
     * @param number The number of the port.
     */
    internal constructor(number: Int) : this(null, number)

    internal fun build(): PortSpec = PortSpec(name, number)
}