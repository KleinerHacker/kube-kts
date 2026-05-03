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

package org.pcsoft.framework.kube.kts.api.chart.template.types

/**
 * Builds and configures metadata specifications for a Kubernetes resource.
 *
 * This builder is responsible for creating instances of [MetadataSpec], which define
 * metadata properties such as the resource name, optionally generated name, and namespace.
 * It is commonly used in the context of defining metadata for Kubernetes resource templates.
 *
 * @constructor Creates an instance with the required name of the resource.
 *              This constructor is internal and should not be accessed directly.
 *
 * @property generateName An optional prefix for generating a unique resource name.
 *                        The server appends a random suffix to this value to create
 *                        a unique name.
 * @property namespace The namespace in which the resource should be created.
 *                     If left null, the resource will be created in the default namespace.
 */
class MetadataSpecBuilder internal constructor(private val name: String) {
    /**
     * An optional prefix, used by the server, to generate a unique name for the resource.
     */
    var generateName: String? = null

    /**
     * The namespace in which the resource should be created.
     */
    var namespace: String? = null

    fun build(): MetadataSpec {
        require(name.isNotBlank()) { "Name is required" }

        return MetadataSpec(name, generateName, namespace)
    }
}