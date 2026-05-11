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
 * Represents HTTP or HTTPS schemes.
 *
 * Scheme is used to define the protocol scheme for URLs or
 * network requests, typically in the context of ingress rules,
 * service endpoints, or API clients.
 *
 * It is often used for distinguishing between unsecured (HTTP)
 * and secured (HTTPS) communication.
 */
enum class ProtocolScheme {
    /**
     * Represents the HTTP protocol scheme.
     *
     * HTTP is a widely used protocol for transferring hypertext and other resources
     * on the web. It operates over a connectionless and stateless model. Commonly used
     * for unsecured (non-encrypted) communication in network configurations.
     */
    HTTP,

    /**
     * Represents the HTTPS protocol scheme.
     *
     * HTTPS is a secured version of the Hypertext Transfer Protocol (HTTP),
     * often used for encrypted communication over the web. It leverages
     * Transport Layer Security (TLS) or Secure Sockets Layer (SSL) protocols
     * to ensure secure data exchange between clients and servers by providing
     * authentication, data integrity, and encryption.
     *
     * Commonly used in scenarios requiring secure communication, such as
     * API interactions, web applications, and service endpoints.
     */
    HTTPS
}