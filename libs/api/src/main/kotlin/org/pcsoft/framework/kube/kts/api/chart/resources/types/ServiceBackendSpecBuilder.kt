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
    private var port: IngressPortSpec? = null

    /**
     * Configures the port for the service backend specification by its name.
     *
     * @param name The name of the port to be used in the backend configuration.
     */
    fun port(name: String) {
        port = IngressPortSpecBuilder(name).build()
    }

    /**
     * Configures the port for the service backend specification by its number.
     *
     * @param number The numeric value of the port to be used in the backend configuration.
     */
    fun port(number: Int) {
        port = IngressPortSpecBuilder(number).build()
    }

    override fun build(): BackendSpec {
        require(name.isNotBlank()) { "Service name is required for service backend" }
        require(port != null) { "Port is required for service backend" }

        return ServiceBackendSpec(name, port!!)
    }
}
