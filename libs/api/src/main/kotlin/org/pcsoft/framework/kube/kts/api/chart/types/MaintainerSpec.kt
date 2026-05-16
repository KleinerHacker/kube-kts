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

package org.pcsoft.framework.kube.kts.api.chart.types

import org.pcsoft.framework.kube.kts.api.intern.NoArgs
import org.pcsoft.framework.kube.kts.api.types.MailAddress
import java.net.URI

/**
 * Represents the specification of a maintainer, including their name, email, and optional URL.
 *
 * @property name The full name of the maintainer.
 * @property email An optional email address for the maintainer, represented as a [MailAddress].
 * @property url An optional URL associated with the maintainer, such as a personal or professional webpage.
 */
@NoArgs
data class MaintainerSpec(
    val name: String,
    val email: MailAddress?,
    val url: URI?
)
