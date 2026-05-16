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

package org.pcsoft.framework.kube.kts.core.merge

import java.nio.file.Path

/**
 * Represents a strategy for merging multiple YAML files into a single resulting YAML structure.
 *
 * Implementations of this interface handle merging logic, which may vary based on the use case, such as
 * merging arrays with specific strategies or overriding base values with overlays.
 */
interface YamlMerging {
    companion object {
        /**
         * Creates a default implementation of the `YamlMerging` interface.
         *
         * This function generates a `YamlMerging` instance using the specified array merge strategy.
         * By default, the strategy for merging YAML array nodes is set to `YamlArrayMergeStrategy.Replace`.
         *
         * @param arrayMergeStrategy The strategy to use for merging YAML array nodes. Defaults to `YamlArrayMergeStrategy.Replace`.
         * @return A `YamlMerging` instance configured with the specified array merge strategy.
         */
        fun createDefault(
            arrayMergeStrategy: YamlArrayMergeStrategy = YamlArrayMergeStrategy.Replace
        ): YamlMerging {
            return DefaultYamlMerging(arrayMergeStrategy)
        }

        /**
         * A predefined implementation of [YamlMerging] that uses the Helm CLI to merge multiple YAML files.
         *
         * This implementation relies on Helm's templating engine to merge the base and overlay YAML files
         * by creating a temporary Helm chart, applying the provided YAML files as value files, and rendering
         * the final merged output.
         *
         * Use this strategy when Helm-specific merging logic is required, such as handling complex configurations
         * in Helm charts that depend on values.yaml files.
         */
        val HELM: YamlMerging = HelmYamlMerging()
    }

    /**
     * Merges a base YAML file with one or more overlay YAML files into a single resulting YAML structure.
     *
     * @param baseYamlIn The path to the base YAML file. If null, the first file in the `overlayYamlIn` parameter is used as the base.
     * @param overlayYamlIn The paths to the overlay YAML files to merge with the base. Can be empty if `baseYamlIn` is provided.
     * @return A string representing the merged YAML content, or null if no files are provided.
     * @throws IllegalArgumentException If any of the provided YAML files (base or overlays) do not exist or are not regular files.
     */
    fun merge(baseYamlIn: Path?, vararg overlayYamlIn: Path): String?
}