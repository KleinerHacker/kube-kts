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

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.intern.jackson.BackendSpecDeserializer
import org.pcsoft.framework.kube.kts.api.intern.jackson.BackendSpecSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Represents a specification for configuring the backend in a resource setup.
 *
 * This sealed class serves as the base type for different backend configurations, such as service-based
 * and resource-based backends. It is designed to be extensible for various backend types and ensures
 * proper (de)serialization through custom serializers and deserializers.
 *
 * @property name The name of the backend as a general identifier applicable across backend types.
 */
@NoArgs
@JsonSerialize(using = BackendSpecSerializer::class)
@JsonDeserialize(using = BackendSpecDeserializer::class)
sealed class BackendSpec(
    val name: String
)

/**
 * Represents a backend that points to a Kubernetes Service.
 *
 * @property name The name of the service.
 * @property port The port configuration for the service backend.
 */
@NoArgs
class ServiceBackendSpec internal constructor(
    name: String,
    val port: PortSpec
) : BackendSpec(name)

/**
 * Represents a backend that points to a generic Kubernetes resource.
 *
 * @property name The name of the resource.
 * @property kind The kind of the resource.
 * @property apiGroup The API group of the resource, if applicable.
 */
@NoArgs
class ResourceBackendSpec internal constructor(
    name: String,
    val kind: String,
    val apiGroup: String?
) : BackendSpec(name)