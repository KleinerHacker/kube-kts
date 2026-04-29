package org.pcsoft.framework.kube.kts.core.merge

import java.nio.file.Path

interface YamlMerging {
    companion object {
        fun createDefault(
            arrayMergeStrategy: YamlArrayMergeStrategy = YamlArrayMergeStrategy.Replace
        ): YamlMerging {
            return DefaultYamlMerging(arrayMergeStrategy)
        }

        val HELM: YamlMerging = HelmYamlMerging()
    }

    fun merge(baseYamlIn: Path?, vararg overlayYamlIn: Path): String?
}