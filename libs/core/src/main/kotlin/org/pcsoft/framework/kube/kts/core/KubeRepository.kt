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

package org.pcsoft.framework.kube.kts.core

/**
 * Represents a repository containing Kubernetes-related files.
 *
 * This interface serves as an abstraction for managing collections of Kubernetes files
 * in a specific repository. It provides access to the repository name, the list of processed files,
 * and any legacy Helm YAML template files. The generic type parameter [T] must extend the [KubeFile] interface,
 * allowing flexibility in specifying the types of files managed within the repository.
 *
 * Typical use cases include storing and managing Kubernetes configuration files, Helm charts,
 * and other resource definitions, while ensuring compatibility with both modern and legacy formats.
 *
 * @param T The type of Kubernetes-related files this repository handles. Must extend [KubeFile].
 */
interface KubeRepository<T : KubeFile> {
    /**
     * The name of the repository.
     */
    val name: String

    /**
     * The list of processed files in this repository.
     */
    val files: List<T>

    /**
     * The list of legacy Helm files (YAML templates) in this repository.
     */
    val legacyFiles: List<LegacyHelmFile>
}