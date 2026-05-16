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

/**
 * Defines the TLS configuration for a resource, including the associated hosts and secret.
 *
 * This class is used to specify Transport Layer Security (TLS) settings,
 * such as the domains to be secured and the name of the Kubernetes secret
 * containing the required TLS certificates and keys.
 *
 * @property hosts A list of hostnames for which TLS is configured. Can be null if no specific hosts are defined.
 * @property secretName The name of the Kubernetes secret that stores the TLS certificate and private key.
 */
@NoArgs
data class TlsSpec(
    val hosts: List<String>?,
    val secretName: String?
)