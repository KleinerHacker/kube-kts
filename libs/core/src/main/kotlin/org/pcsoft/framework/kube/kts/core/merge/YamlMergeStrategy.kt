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