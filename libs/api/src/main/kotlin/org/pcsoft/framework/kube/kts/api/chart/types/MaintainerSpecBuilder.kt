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

package org.pcsoft.framework.kube.kts.api.chart.types

import org.pcsoft.framework.kube.kts.api.types.MailAddress
import java.net.URI

/**
 * A builder for constructing instances of [MaintainerSpec].
 *
 * This builder provides properties to set the email address and URL for a maintainer.
 * The maintainer's name must be provided when the builder is created.
 *
 * @constructor Creates a builder with the specified maintainer name.
 * @param name The full name of the maintainer.
 */
class MaintainerSpecBuilder internal constructor(private val name: String) {
    /**
     * The maintainer's email address.
     */
    var email: MailAddress? = null

    /**
     * The URL of the maintainer's homepage or profile.
     */
    var url: URI? = null

    internal fun build(): MaintainerSpec = MaintainerSpec(name, email, url)
}