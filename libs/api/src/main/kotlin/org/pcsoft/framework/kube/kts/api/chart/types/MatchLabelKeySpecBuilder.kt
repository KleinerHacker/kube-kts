/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart.types

/**
 * A builder class responsible for constructing a specification based on match label keys.
 * This class allows adding single or multiple keys to define the match label specification.
 * It is designed for internal use and provides controlled access to label key configurations.
 */
class MatchLabelKeySpecBuilder internal constructor() {
    private val keys: MutableList<String> = mutableListOf()

    /**
     * Adds a match label key to the builder. If the keys list is uninitialized, it initializes it first.
     *
     * @param key The match label key to be added.
     */
    fun key(key: String) {
        this.keys.add(key)
    }

    /**
     * Adds one or more match label keys to the builder. If the keys list is uninitialized, it initializes it first.
     *
     * @param keys The match label keys to be added.
     */
    fun keys(vararg keys: String) {
        this.keys.addAll(keys)
    }

    /**
     * Constructs and returns a MatchLabelKeySpec instance based on the currently configured keys.
     *
     * @return A MatchLabelKeySpec instance containing the list of match label keys,
     *         or null if no keys were added to the builder.
     */
    internal fun build(): MatchLabelKeySpec =
        MatchLabelKeySpec(keys)
}