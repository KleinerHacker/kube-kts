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