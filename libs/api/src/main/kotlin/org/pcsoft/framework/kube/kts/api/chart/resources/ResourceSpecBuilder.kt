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

package org.pcsoft.framework.kube.kts.api.chart.resources

/**
 * Represents a builder interface for creating instances of specific Kubernetes resource specifications.
 *
 * Implementations of this interface are responsible for providing customized
 * configurations for Kubernetes resources by building instances of the specified
 * type that extends `ResourceSpec`.
 *
 * @param S The type of the resource specification that this builder creates. It must
 *          extend the `ResourceSpec` interface.
 */
interface ResourceSpecBuilder<S : ResourceSpec> {
    /**
     * Builds and returns an instance of the resource specification.
     *
     * @return An instance of type S representing the resource specification.
     */
    fun build(): S
}