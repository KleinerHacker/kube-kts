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

package org.pcsoft.framework.kube.kts.api.chart.resources

import org.pcsoft.framework.kube.kts.api.chart.KubeSpec

/**
 * Represents a generic marker interface for Kubernetes resource specifications.
 *
 * Classes implementing this interface define the structure and configuration
 * of various Kubernetes resources, serving as a contract for resource-related
 * specifications in the domain of Kubernetes API integration.
 *
 * This interface extends the base `KubeSpec` interface, enabling its implementations
 * to act as Kubernetes resource specifications, such as those for ingress, service,
 * pod, or deployment configurations.
 */
interface ResourceSpec : KubeSpec