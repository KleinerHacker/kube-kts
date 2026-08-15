/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.pcsoft.framework.kube.kts.api.chart.types.MatchLabelKeySpecBuilder

/**
 * A builder class for creating specifications for `PodAffinityTerm`.
 *
 * This class allows you to configure the topology key, label selectors, namespaces,
 * namespace selectors, and match or mismatch label keys for setting up pod affinity
 * and anti-affinity configurations in Kubernetes scheduling.
 *
 * @constructor
 * This class is internally constructed with a mandatory `topologyKey` parameter.
 */
class PodAffinityTermSpecBuilder internal constructor(private val topologyKey: String) {
    private var labelSelector: LabelSelectorSpecBuilder? = null
    private var namespaces: MutableList<String>? = null
    private var namespaceSelector: LabelSelectorSpecBuilder? = null
    private var matchLabelKeys: MatchLabelKeySpecBuilder? = null
    private var mismatchLabelKeys: MatchLabelKeySpecBuilder? = null

    /**
     * Configures the label selector for the pod affinity term specification.
     *
     * @param setup A lambda with receiver to build the configuration for the label selector using
     *              the provided [LabelSelectorSpecBuilder].
     * 
     * Example:
     * ```kotlin
     * labelSelector {
     *     matchLabels("app" to "frontend", "tier" to "web")
     * }
     * ```
     */
    fun labelSelector(setup: LabelSelectorSpecBuilder.() -> Unit) {
        labelSelector = LabelSelectorSpecBuilder().apply(setup)
    }

    /**
     * Adds a namespace to the list of namespaces in the PodAffinityTermSpecBuilder.
     *
     * @param namespace The namespace to be added.
     */
    fun addNamespace(namespace: String) {
        if (namespaces == null) {
            namespaces = mutableListOf()
        }
        namespaces?.add(namespace)
    }

    /**
     * Adds one or more namespaces to the list of namespaces in the PodAffinityTermSpecBuilder.
     *
     * @param namespaces A variable number of namespaces to be added.
     */
    fun addNamespaces(vararg namespaces: String) {
        if (this.namespaces == null) {
            this.namespaces = mutableListOf()
        }
        this.namespaces?.addAll(namespaces.toList())
    }

    /**
     * Configures the namespaces for the pod affinity term specification. 
     * Allows adding one or more namespaces using the provided [NamespaceListBuilder].
     *
     * @param setup A lambda with receiver to build the list of namespaces 
     *              using the [NamespaceListBuilder].
     * 
     * Example:
     * ```kotlin
     * namespaces {
     *     namespace("default")
     *     namespaces("kube-system", "production")
     * }
     * ```
     */
    fun namespaces(setup: NamespaceListBuilder.() -> Unit) =
        NamespaceListBuilder().apply(setup)

    /**
     * Configures the namespace selector for the pod affinity term specification.
     *
     * @param setup A lambda with receiver to build the configuration for the namespace selector 
     *              using the provided [LabelSelectorSpecBuilder].
     * 
     * Example:
     * ```kotlin
     * namespaceSelector {
     *     matchLabels("environment" to "production")
     * }
     * ```
     */
    fun namespaceSelector(setup: LabelSelectorSpecBuilder.() -> Unit) {
        namespaceSelector = LabelSelectorSpecBuilder().apply(setup)
    }

    /**
     * Adds a match label key to the pod affinity term specification.
     * If the internal match label keys builder is uninitialized, it will initialize a new instance
     * of [MatchLabelKeySpecBuilder], then adds the provided key to it.
     *
     * @param key The match label key to be added.
     */
    fun addMatchLabelKey(key: String) {
        if (matchLabelKeys == null) {
            matchLabelKeys = MatchLabelKeySpecBuilder()
        }
        matchLabelKeys?.key(key)
    }

    /**
     * Adds one or more match label keys to the pod affinity term specification. 
     * If the internal match label keys builder is uninitialized, it initializes a new instance 
     * of [MatchLabelKeySpecBuilder], then adds the provided keys to it.
     *
     * @param keys A variable number of match label keys to be added.
     */
    fun addMatchLabelKeys(vararg keys: String) {
        if (matchLabelKeys == null) {
            matchLabelKeys = MatchLabelKeySpecBuilder()
        }
        matchLabelKeys?.keys(*keys)
    }

    /**
     * Adds a mismatch label key to the pod affinity term specification.
     * If the internal mismatch label keys builder is uninitialized, it initializes a new instance
     * of [MatchLabelKeySpecBuilder], then adds the provided key to it.
     *
     * @param key The mismatch label key to be added.
     */
    fun addMismatchLabelKey(key: String) {
        if (mismatchLabelKeys == null) {
            mismatchLabelKeys = MatchLabelKeySpecBuilder()
        }
        mismatchLabelKeys?.key(key)
    }

    /**
     * Adds one or more mismatch label keys to the pod affinity term specification.
     * If the internal mismatch label keys builder is uninitialized, it initializes a new instance
     * of [MatchLabelKeySpecBuilder], then adds the provided keys to it.
     *
     * @param keys A variable number of mismatch label keys to be added.
     */
    fun addMismatchLabelKeys(vararg keys: String) {
        if (mismatchLabelKeys == null) {
            mismatchLabelKeys = MatchLabelKeySpecBuilder()
        }
        mismatchLabelKeys?.keys(*keys)
    }

    /**
     * Configures the match label keys for the pod affinity term specification.
     * This method is designed to streamline the setup process for defining match label keys
     * by using a lambda with a [MatchLabelKeySpecBuilder] receiver.
     *
     * @param setup A lambda with receiver to build the match label key configuration
     *              using the provided [MatchLabelKeySpecBuilder].
     * 
     * Example:
     * ```kotlin
     * matchLabelKeys {
     *     key("app")
     *     keys("tier", "version")
     * }
     * ```
     */
    fun matchLabelKeys(setup: MatchLabelKeySpecBuilder.() -> Unit) {
        matchLabelKeys = MatchLabelKeySpecBuilder().apply(setup)
    }

    /**
     * Configures the mismatch label keys for the pod affinity term specification.
     * This method allows defining mismatch label keys by using a lambda with a 
     * [MatchLabelKeySpecBuilder] receiver to streamline the setup process.
     *
     * @param setup A lambda with receiver to build the mismatch label key configuration
     *              using the provided [MatchLabelKeySpecBuilder].
     * 
     * Example:
     * ```kotlin
     * mismatchLabelKeys {
     *     key("excluded-label")
     *     keys("debug", "test")
     * }
     * ```
     */
    fun mismatchLabelKeys(setup: MatchLabelKeySpecBuilder.() -> Unit) {
        mismatchLabelKeys = MatchLabelKeySpecBuilder().apply(setup)
    }

    /**
     * Builds a `PodAffinityTermSpec` instance based on the current state of the `PodAffinityTermSpecBuilder`.
     *
     * Combines the configured parameters, including the topology key, label selector, namespaces,
     * namespace selector, match label keys, and mismatch label keys, to construct the final `PodAffinityTermSpec`.
     *
     * @return A new `PodAffinityTermSpec` instance with the specified configuration.
     */
    internal fun build() = PodAffinityTermSpec(
        topologyKey,
        labelSelector?.build(),
        namespaces?.toList(),
        namespaceSelector?.build(),
        matchLabelKeys?.build(),
        mismatchLabelKeys?.build()
    )

    /**
     * A builder class for managing a list of namespaces in the context of a PodAffinityTerm specification.
     * This class provides methods to add single or multiple namespaces to the configuration.
     */
    inner class NamespaceListBuilder internal constructor() {
        /**
         * Adds a namespace to the list of namespaces in the current context.
         *
         * @param namespace The namespace to be added.
         */
        fun namespace(namespace: String) = addNamespace(namespace)

        /**
         * Adds one or more namespaces to the list of namespaces in the current context.
         *
         * @param namespaces A variable number of namespace strings to be added.
         */
        fun namespaces(vararg namespaces: String) = addNamespaces(*namespaces)
    }
}
