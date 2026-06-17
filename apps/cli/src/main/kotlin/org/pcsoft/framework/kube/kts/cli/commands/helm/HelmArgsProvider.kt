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

package org.pcsoft.framework.kube.kts.cli.commands.helm

/**
 * Contract for option groups (picocli mixins) that contribute arguments to the Helm command line.
 *
 * Each implementation translates only the options that were actually set by the user into the
 * corresponding Helm CLI arguments via [toHelmArgs]. Options left at their default value must not
 * produce any output, so that nothing unintended is forwarded to Helm.
 */
interface HelmArgsProvider {
    /**
     * Builds the list of Helm CLI arguments represented by this option group.
     *
     * Only options explicitly provided by the user are included. Boolean flags are emitted just when
     * set to `true`, nullable single values only when not `null`, and multi-value options as one
     * name/value pair per element.
     *
     * @return the Helm arguments for this group, or an empty list if nothing was set.
     */
    fun toHelmArgs(): List<String>

    /**
     * Collects Helm arguments into a fresh list via the argument-building helpers.
     *
     * Declared as a member so the helpers ([flag], [opt], [multi]) are only available to
     * [HelmArgsProvider] implementations and cannot be used from arbitrary code.
     *
     * @param block the configuration block appending arguments.
     * @return the assembled argument list.
     */
    fun helmArgs(block: MutableList<String>.() -> Unit): List<String> =
        mutableListOf<String>().apply(block)

    /**
     * Appends a boolean Helm flag (e.g. `--atomic`) when [condition] is `true`.
     *
     * @param name the long flag name including its leading dashes.
     * @param condition whether the flag was enabled by the user.
     */
    fun MutableList<String>.flag(name: String, condition: Boolean) {
        if (condition) add(name)
    }

    /**
     * Appends a Helm option with a single value (e.g. `--namespace test`) when [value] is not `null`.
     *
     * @param name the long flag name including its leading dashes.
     * @param value the user-provided value, or `null` when the option was not set.
     */
    fun MutableList<String>.opt(name: String, value: Any?) {
        if (value != null) {
            add(name)
            add(value.toString())
        }
    }

    /**
     * Appends a repeatable Helm option (e.g. `--set a=1 --set b=2`), one name/value pair per element.
     *
     * @param name the long flag name including its leading dashes.
     * @param values the user-provided values, or `null`/empty when the option was not set.
     */
    fun MutableList<String>.multi(name: String, values: Array<String>?) {
        values?.forEach {
            add(name)
            add(it)
        }
    }
}
