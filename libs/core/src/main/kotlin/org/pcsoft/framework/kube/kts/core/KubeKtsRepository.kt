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
 * Represents a repository containing Kubernetes Kotlin Script (KTS) files.
 *
 * This class implements the [KubeRepository] interface to encapsulate
 * a collection of Kubernetes-related Kotlin Script files ([KubeKtsFile])
 * along with any associated legacy Helm files ([LegacyHelmFile]).
 *
 * It is designed to provide a structured way to manage and interact
 * with repositories comprising modern Kotlin Script-based definitions
 * and legacy YAML templates for Kubernetes resources.
 *
 * @constructor Internal constructor used for instantiating the repository.
 *
 * @property name The name of the repository.
 * @property files The list of processed Kubernetes Kotlin Script files in the repository.
 * @property legacyFiles The list of legacy Helm files (YAML templates) in the repository.
 */
class KubeKtsRepository internal constructor(
    override val name: String,
    override val files: List<KubeKtsFile>,
    override val legacyFiles: List<LegacyHelmFile>
) : KubeRepository<KubeKtsFile> {

    /**
     * Returns the string representation of the repository.
     *
     * @return The name of the repository.
     */
    override fun toString(): String {
        return name
    }
}