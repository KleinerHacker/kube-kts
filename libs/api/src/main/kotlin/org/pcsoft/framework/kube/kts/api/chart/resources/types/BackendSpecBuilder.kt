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
 * Builder class for constructing instances of `BackendSpec`.
 *
 * This sealed class provides an abstraction for creating
 * specific types of `BackendSpec`, such as `ServiceBackendSpec` or
 * `ResourceBackendSpec`. Each implementation is responsible for
 * defining the concrete logic for building a `BackendSpec` instance.
 *
 * The builder pattern ensures that `BackendSpec` objects are
 * created in a controlled manner, allowing for validation and
 * extensibility of backend configuration.
 */
sealed class BackendSpecBuilder {
    internal abstract fun build(): BackendSpec
}

/**
 * Builder for creating an instance of `ServiceBackendSpec`.
 *
 * This class facilitates the construction of a backend specification that points to
 * a Kubernetes Service, including the associated service name and port configuration.
 *
 * @constructor Instantiates a builder for a `ServiceBackendSpec` with the given service name.
 * The constructor is internal as the class is designed for controlled creation within the API.
 *
 * @property name The name of the Kubernetes Service to be used in the backend specification.
 */
class ServiceBackendSpecBuilder internal constructor(private val name: String) : BackendSpecBuilder() {
    private var port: PortSpec? = null

    /**
     * Configures the port for the service backend specification by its name.
     *
     * @param name The name of the port to be used in the backend configuration.
     */
    fun port(name: String) {
        port = PortSpecBuilder(name).build()
    }

    /**
     * Configures the port for the service backend specification by its number.
     *
     * @param number The numeric value of the port to be used in the backend configuration.
     */
    fun port(number: Int) {
        port = PortSpecBuilder(number).build()
    }

    override fun build(): BackendSpec {
        require(name.isNotBlank()) { "Service name is required for service backend" }
        require(port != null) { "Port is required for service backend" }

        return ServiceBackendSpec(name, port!!)
    }
}

/**
 * Builder for creating a resource-based backend specification.
 *
 * This builder is used to configure a backend that points to a generic Kubernetes resource.
 * It requires the resource's name and kind as mandatory parameters and allows optional API
 * group configuration. The resulting `ResourceBackendSpec` object can be used to represent
 * Kubernetes resources as backends in various configurations.
 *
 * @constructor Creates a new instance of the builder with the specified resource name and kind.
 * @param name The name of the resource.
 * @param kind The kind of the resource to be referenced in the backend.
 *
 * @see BackendSpecBuilder
 * @see ResourceBackendSpec
 */
class ResourceBackendSpecBuilder internal constructor(private val name: String, private val kind: String) :
    BackendSpecBuilder() {
    /**
     * The API group of the resource.
     */
    var apiGroup: String? = null

    override fun build(): BackendSpec {
        require(name.isNotBlank()) { "Resource name is required for resource backend" }
        require(kind.isNotBlank()) { "Resource kind is required for resource backend" }

        return ResourceBackendSpec(name, kind, apiGroup)
    }
}