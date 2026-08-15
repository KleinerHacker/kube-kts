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
