package org.pcsoft.framework.kube.kts.core.merge

import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

abstract class YamlMergingBase : YamlMerging {
    final override fun merge(baseYamlIn: Path?, vararg overlayYamlIn: Path): String? {
        if (baseYamlIn == null && overlayYamlIn.isEmpty())
            return null

        val baseYaml = baseYamlIn ?: overlayYamlIn.first()
        val overlayYaml = if (baseYamlIn != null) overlayYamlIn else overlayYamlIn.drop(1).toTypedArray()

        require(baseYaml.exists() && baseYaml.isRegularFile()) { "Base YAML file does not exist: ${baseYaml.toAbsolutePath()}" }
        overlayYaml.forEach {
            require(it.exists() && it.isRegularFile()) { "Overlay YAML file does not exist: ${it.toAbsolutePath()}" }
        }

        return doMerge(baseYaml, overlayYaml)
    }

    protected abstract fun doMerge(baseYaml: Path, overlayYaml: Array<out Path>): String
}