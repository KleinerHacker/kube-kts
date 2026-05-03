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

package org.pcsoft.framework.kube.kts.core.renderer

import org.pcsoft.framework.kube.kts.core.KubeHelmFile

/**
 * Defines the contract for rendering Kubernetes Helm files into string representations.
 *
 * Implementations of this interface are responsible for processing instances of [KubeHelmFile]
 * and transforming their associated specification objects into a serialized string format,
 * typically for deployment or further processing.
 *
 * The companion object provides a default implementation, [DEFAULT], which utilizes a standard
 * rendering approach to convert Helm files into YAML strings. This is intended for typical use cases
 * where customization is not required.
 */
interface KubeHelmRenderer {
    companion object {
        /**
         * Represents the default implementation of [KubeHelmRenderer].
         *
         * This default renderer uses the [DefaultKubeHelmRenderer] to provide a standard mechanism
         * for rendering instances of [KubeHelmFile]. Specifically, it converts the file's associated
         * specification object into a serialized YAML string format, suitable for deployment or other
         * downstream applications.
         *
         * The default implementation is preconfigured and works out-of-the-box for common rendering
         * requirements, ensuring a consistent and reliable output.
         */
        val DEFAULT: KubeHelmRenderer = DefaultKubeHelmRenderer
    }

    /**
     * Renders the provided Kubernetes Helm file into a string representation.
     *
     * This method takes a [KubeHelmFile] instance, which includes an evaluated specification object,
     * and converts it into a serialized string format. Typically, this is used to produce YAML output
     * or other serialized forms suitable for deployment or further processing.
     *
     * @param file the [KubeHelmFile] instance to render, containing the specification to be serialized
     * @return the serialized string representation of the given [KubeHelmFile]
     */
    fun render(file: KubeHelmFile): String
}