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