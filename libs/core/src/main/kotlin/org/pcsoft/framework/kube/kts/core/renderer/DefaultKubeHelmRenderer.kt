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
import org.pcsoft.framework.kube.kts.logging.logger
import org.pcsoft.framework.kube.kts.logging.successStyle
import org.pcsoft.framework.kube.kts.logging.symbolSubProcess

/**
 * Default renderer for Kubernetes Helm files, converting their specifications into YAML string format.
 *
 * This implementation extends the [KubeHelmRendererBase] class, leveraging its preconfigured [tools.jackson.dataformat.yaml.YAMLMapper]
 * for serializing the specifications of [KubeHelmFile] instances. The rendered YAML output can be used
 * for deployment or other downstream processes.
 *
 * The renderer logs debugging information during the rendering process, including the subject of the file
 * being rendered and the resulting output. Trace-level logging is also applied to indicate successful rendering,
 * with styling applied to the logged messages.
 */
internal object DefaultKubeHelmRenderer : KubeHelmRendererBase() {
    private val logger = logger()

    override fun render(file: KubeHelmFile): String {
        logger.atDebug().log { "$symbolSubProcess Rendering to YAML: ${file.subject}" }

        return mapper.writeValueAsString(file.spec).apply {
            logger.atTrace().log { "Rendered: ${file.subject}".successStyle() }
        }
    }
}