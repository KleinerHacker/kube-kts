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

package org.pcsoft.framework.kube.kts.core.scanner

import org.pcsoft.framework.kube.kts.core.KubeKtsRepository
import java.nio.file.Path

/**
 * Interface for scanning a repository containing Kubernetes Kotlin Script (KTS) files.
 *
 * This interface defines the contract for implementing a repository scanner
 * that can identify and process Kubernetes-related files, including both
 * modern Kotlin Scripts and legacy Helm YAML templates, within a specified directory path.
 *
 * The scanner implementation processes the repository structure to identify
 * valid Kubernetes Kotlin Script files (.kts) and YAML templates (.yaml, .yml, .tpl)
 * and organizes them into a [KubeKtsRepository] object, representing the repository.
 *
 * The default implementation is provided in [DefaultKubeKtsRepositoryScanner].
 */
interface KubeKtsRepositoryScanner {
    companion object {
        /**
         * The default implementation of the [KubeKtsRepositoryScanner].
         *
         * This property provides a pre-configured instance of [DefaultKubeKtsRepositoryScanner],
         * which can be used to scan repositories containing Kubernetes Kotlin Script (.kts) files,
         * as well as legacy Helm YAML templates (.yaml, .yml, .tpl).
         *
         * The scanner assesses the repository structure, identifies valid files, and organizes them
         * into a [KubeKtsRepository] object, encapsulating the discovered Kubernetes resources.
         *
         * Use this property when a standard scanning implementation is enough for your use case.
         */
        val DEFAULT: KubeKtsRepositoryScanner = DefaultKubeKtsRepositoryScanner
    }

    /**
     * Scans the specified directory path for Kubernetes Kotlin Script (KTS) files and legacy Helm templates,
     * organizing them into a [KubeKtsRepository].
     *
     * The scanner processes the given path to identify regular files with supported extensions (.kts for Kubernetes
     * Kotlin Scripts and .yaml/.yml/.tpl for legacy Helm files). The discovered files are grouped into a repository
     * that encapsulates the modern and legacy Kubernetes resources.
     *
     * @param path The directory path to scan for Kubernetes-related files. The path must exist and be a directory.
     * @return A [KubeKtsRepository] representing the structure and contents of the scanned repository.
     * @throws IllegalArgumentException If the path does not exist, is not a directory, or lacks a required chart file.
     */
    fun scan(path: Path): KubeKtsRepository
}