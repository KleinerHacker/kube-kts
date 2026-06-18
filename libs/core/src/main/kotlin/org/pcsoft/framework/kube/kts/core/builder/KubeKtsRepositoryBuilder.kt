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

package org.pcsoft.framework.kube.kts.core.builder

import org.pcsoft.framework.kube.kts.api.chart.KubeSpec
import org.pcsoft.framework.kube.kts.core.DefaultKubeHelmFile
import org.pcsoft.framework.kube.kts.core.KubeHelmFile
import org.pcsoft.framework.kube.kts.core.KubeHelmRepository
import org.pcsoft.framework.kube.kts.core.KubeKtsFile
import org.pcsoft.framework.kube.kts.core.KubeKtsRepository
import org.pcsoft.framework.kube.kts.core.merge.YamlMerging
import java.nio.file.Path

/**
 * Builder interface for creating instances of a [KubeHelmRepository].
 *
 * This interface provides functionality to configure and construct repositories
 * that handle the management and processing of Kubernetes YAML resources and Helm charts,
 * based on Kotlin script inputs. The default implementation uses a combination of a
 * script processor, YAML merging strategy, and a Helm file mapper to process and
 * build the repository.
 *
 * The companion object provides a factory method for constructing a default builder instance
 * with configurable parameters for processing Kotlin scripts, merging YAML files,
 * and mapping files to Helm artifacts.
 */
interface KubeKtsRepositoryBuilder {
    companion object {
        /**
         * Creates a default implementation of [KubeKtsRepositoryBuilder] with configurable parameters for
         * Kotlin script processing, YAML merging strategy, unsafe script execution, and Helm file mapping.
         *
         * @param processor The [KotlinScriptProcessor] instance to use for compiling and executing Kotlin scripts.
         *                  Defaults to [KotlinScriptProcessor.DEFAULT].
         * @param merging The [YamlMerging] strategy to use for merging YAML files. Defaults to [YamlMerging.HELM].
         * @param unsafe If true, executes Kotlin scripts in an unsafe mode, potentially bypassing security checks.
         *               Defaults to false.
         * @param helmFileMapper A lambda function that maps a [KubeKtsFile] and `KubeSpec` to a [KubeHelmFile].
         *                       Defaults to a mapping generating a [DefaultKubeHelmFile].
         * @return A [KubeKtsRepositoryBuilder] instance configured with the provided parameters.
         */
        fun createDefault(
            processor: KotlinScriptProcessor = KotlinScriptProcessor.DEFAULT,
            merging: YamlMerging = YamlMerging.HELM,
            unsafe: Boolean = false,
            helmFileMapper: (KubeKtsFile, KubeSpec) -> KubeHelmFile = { file, spec ->
                DefaultKubeHelmFile(file, spec)
            }
        ): KubeKtsRepositoryBuilder =
            DefaultKubeKtsRepositoryBuilder(processor, merging, unsafe, helmFileMapper)
    }

    /**
     * Builds a new instance of [KubeHelmRepository] based on the provided [KubeKtsRepository] and an array of value files.
     *
     * @param repository The source repository containing Kubernetes Kotlin Script (KTS) files and legacy Helm files.
     * @param valueFiles An array of filesystem paths pointing to the value files used to generate the resulting Helm repository.
     * @return A new instance of [KubeHelmRepository] containing Helm-based Kubernetes files and any legacy Helm files.
     */
    fun build(repository: KubeKtsRepository, valueFiles: Array<Path>): KubeHelmRepository
}