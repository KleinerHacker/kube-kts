package org.pcsoft.framework.kube.kts.core.merge

/**
 * Defines the strategies for merging YAML array nodes when processing multiple YAML structures.
 *
 * The strategy specifies how elements of arrays in the base YAML document should interact with elements
 * of arrays in overlay YAML documents during the merge operation.
 */
enum class YamlArrayMergeStrategy {
    /**
     * Specifies that no merge should occur for YAML array nodes.
     *
     * When the `None` strategy is used, the original array in the base YAML document remains
     * unchanged, and any elements from the overlay YAML documents are not merged into the base array.
     *
     * This strategy effectively disables merging for YAML arrays.
     */
    None,

    /**
     * Represents a strategy for merging YAML array nodes where new elements from the overlay arrays
     * are added to the beginning of the base array.
     *
     * When using this strategy, every element from the overlay array is inserted at the start of the
     * base array in the same order they appear in the overlay.
     */
    AddFirst,

    /**
     * Represents a strategy for merging YAML array nodes where new elements from the overlay arrays
     * are added to the end of the base array.
     *
     * When using this strategy, all elements from the overlay arrays are appended to the base array in
     * the same order they appear in the overlay.
     */
    AddLast,

    /**
     * Specifies a strategy for merging YAML array nodes where the existing array in the base YAML document
     * is fully replaced by the array from the overlay YAML document.
     *
     * When using the `Replace` strategy, all elements in the base array are removed and replaced with the
     * elements from the overlay array in their original order. This ensures that the resulting array is
     * an exact copy of the overlay array, discarding any prior elements in the base array.
     *
     * This strategy is useful when the overlay array should completely override the base array content.
     */
    Replace
}

/**
 * Defines the strategies for merging YAML object nodes when processing multiple YAML structures.
 *
 * The strategy specifies how object fields in the base YAML document should interact with fields
 * from overlay YAML documents during the merge operation.
 */
enum class YamlObjectMergeStrategy {
    /**
     * Represents the "None" option for the YAML object merge strategy.
     *
     * When this strategy is used, no merging operations are performed on object fields.
     * Any existing fields in the base YAML document remain unchanged, and fields from
     * overlay YAML documents are ignored.
     */
    None,

    /**
     * Represents a merge strategy for YAML object nodes during the merging process.
     *
     * The merge operation determines how fields in the base YAML document interact with
     * fields from overlay YAML documents. This strategy typically applies in cases where
     * multiple YAML configurations need to be combined into a single result.
     */
    Merge,

    /**
     * Specifies the "Replace" strategy for merging YAML object nodes.
     *
     * When this strategy is used, fields in overlay YAML documents replace
     * corresponding fields in the base YAML document during the merge process.
     * If a field exists in both the base and overlay documents, the value from
     * the overlay document will take precedence and overwrite the value in the base document.
     */
    Replace
}

/**
 * Represents the strategies for adding new fields to a base YAML object during the merge process.
 *
 * This strategy specifies how fields that exist in overlay YAML documents but are absent in the base YAML document
 * are handled. It determines whether such fields should be added to the base YAML object or ignored altogether.
 */
enum class YamlNewObjectStrategy {
    /**
     * A strategy indicating that new fields present in overlay YAML documents but absent in the base YAML document
     * should never be added during the merge process.
     *
     * When this strategy is applied, any field in the overlay that does not already exist in the base YAML
     * will be ignored, and the original base YAML structure will remain unmodified regarding such fields.
     */
    AddNever,

    /**
     * A strategy indicating that new fields present in the overlay YAML documents but absent in the base YAML document
     * should always be added during the merge process.
     *
     * When this strategy is applied, any fields in the overlay that do not exist in the base YAML object
     * will be added to the base, ensuring that the base YAML structure includes all fields defined in the overlay.
     */
    AddAlways
}

/**
 * Defines the strategy for merging scalar (non-collection) YAML values during a merge operation.
 *
 * The strategy determines how a scalar value in an overlay YAML document
 * interacts with the corresponding scalar value in the base YAML document.
 */
enum class YamlValueMergeStrategy {
    /**
     * Specifies that no merge should occur for the scalar (non-collection) YAML values
     * during the merge operation.
     *
     * When this option is selected, the scalar value from the base YAML document is retained,
     * and the corresponding value from the overlay YAML document is ignored.
     */
    None,

    /**
     * Specifies that the scalar (non-collection) YAML value from the overlay should replace
     * the corresponding scalar value in the base YAML during the merge operation.
     *
     * When this strategy is applied, the value in the base YAML document is overwritten
     * by the value from the overlay YAML document, regardless of its original value.
     */
    Replace
}