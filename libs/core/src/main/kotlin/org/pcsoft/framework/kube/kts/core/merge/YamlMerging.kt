package org.pcsoft.framework.kube.kts.core.merge

interface YamlMerging {
    companion object {
        fun createDefault(
            arrayMergeStrategy: YamlArrayMergeStrategy = YamlArrayMergeStrategy.AddLast,
            objectMergeStrategy: YamlObjectMergeStrategy = YamlObjectMergeStrategy.Merge,
            newObjectStrategy: YamlNewObjectStrategy = YamlNewObjectStrategy.AddAlways,
            valueMergeStrategy: YamlValueMergeStrategy = YamlValueMergeStrategy.Replace
        ): YamlMerging {
            return DefaultYamlMerging(arrayMergeStrategy, objectMergeStrategy, newObjectStrategy, valueMergeStrategy)
        }
    }

    fun merge(baseYaml: String, vararg overlayYaml: String): String
}