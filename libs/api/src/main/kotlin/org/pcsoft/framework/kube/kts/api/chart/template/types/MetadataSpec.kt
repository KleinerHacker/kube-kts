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

package org.pcsoft.framework.kube.kts.api.chart.template.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs

/**
 * Represents the metadata for a Kubernetes resource.
 *
 * @property name The name of the resource.
 * @property generateName An optional prefix, used by the server to generate a unique name for the resource.
 * @property namespace The namespace in which the resource should be created.
 */
@NoArgs
data class MetadataSpec(val name: String, val generateName: String?, val namespace: String?)