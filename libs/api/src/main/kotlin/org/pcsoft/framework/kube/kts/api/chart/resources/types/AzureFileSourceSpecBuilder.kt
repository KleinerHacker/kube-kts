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
 * Builder for an [AzureFileSourceSpec].
 *
 * @constructor Creates a builder for the given Azure Files share.
 * @param secretName The Secret holding the storage account credentials.
 * @param shareName  The name of the share.
 */
class AzureFileSourceSpecBuilder internal constructor(
    private val secretName: String,
    private val shareName: String
) : SourceSpecBuilder<AzureFileSourceSpec> {
    /**
     * If true, the share is mounted read-only.
     */
    var readOnly: Boolean? = null

    /**
     * Builds the configured Azure Files source.
     *
     * @return An [AzureFileSourceSpec] carrying the configured values.
     */
    override fun build(): AzureFileSourceSpec = AzureFileSourceSpec(secretName, shareName, readOnly)
}
