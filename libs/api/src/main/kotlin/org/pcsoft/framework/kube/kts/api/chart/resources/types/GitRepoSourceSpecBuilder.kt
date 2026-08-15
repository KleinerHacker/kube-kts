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
 * Builder for a [GitRepoSourceSpec].
 *
 * @constructor Creates a builder for the given repository.
 * @param repository The URL of the repository.
 */
class GitRepoSourceSpecBuilder internal constructor(private val repository: String) :
    SourceSpecBuilder<GitRepoSourceSpec> {
    /**
     * The commit hash to check out. Defaults to the repository's default branch when unset.
     */
    var revision: String? = null

    /**
     * The target directory relative to the volume root.
     */
    var directory: String? = null

    /**
     * Builds the configured Git repository source.
     *
     * @return A [GitRepoSourceSpec] carrying the configured values.
     */
    override fun build(): GitRepoSourceSpec = GitRepoSourceSpec(repository, revision, directory)
}
