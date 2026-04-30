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
