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
 * Builder for an [ImageSourceSpec].
 *
 * @constructor Creates a builder for the given image.
 * @param reference The image reference.
 */
class ImageSourceSpecBuilder internal constructor(private val reference: String) :
    SourceSpecBuilder<ImageSourceSpec> {
    /**
     * Controls when the image is pulled.
     */
    var pullPolicy: ContainerSpec.ImagePullPolicy? = null

    /**
     * Builds the configured image source.
     *
     * @return An [ImageSourceSpec] carrying the configured values.
     */
    override fun build(): ImageSourceSpec = ImageSourceSpec(reference, pullPolicy)
}
