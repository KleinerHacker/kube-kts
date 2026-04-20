package org.pcsoft.framework.kube.kts.core

class KubeHelmRepository internal constructor(override val files: List<KubeHelmFile>) : KubeRepository<KubeHelmFile>