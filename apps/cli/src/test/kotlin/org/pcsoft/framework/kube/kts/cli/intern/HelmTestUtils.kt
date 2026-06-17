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

package org.pcsoft.framework.kube.kts.cli.intern

import org.junit.jupiter.api.Assertions
import org.pcsoft.framework.kube.kts.cli.GlobalFlags
import org.pcsoft.framework.kube.kts.cli.MainCommand
import org.pcsoft.framework.kube.kts.cli.commands.BaseHelmCommand
import picocli.CommandLine

/** A repository path that exists in the test resources; used so positional parsing succeeds. */
const val TEST_REPOSITORY = "src/test/resources/kts/helm"

/** Path to a real values overlay file used to exercise the `-f`/`--values` forwarding. */
val TEST_VALUES_FILE: String =
    java.nio.file.Path.of(object {}.javaClass.getResource("/values-overlay.yaml")!!.toURI()).toString()

/**
 * Parses the given CLI arguments without executing the command and returns the Helm command line
 * that would be passed to the `helm` executable.
 *
 * No real Helm process is started: only [BaseHelmCommand.buildHelmCommandLine] is evaluated, so this
 * verifies that all flags are translated and forwarded correctly.
 */
fun buildHelmCommandLine(vararg args: String): List<String> {
    // MainCommand is a singleton object whose @Mixin state is reused across CommandLine instances;
    // reset it so global flags (e.g. --debug) do not leak between tests.
    MainCommand.globalFlags = GlobalFlags()
    val parseResult = CommandLine(MainCommand).parseArgs(*args)
    val subResult = parseResult.subcommand()
        ?: error("No subcommand was parsed for arguments: ${args.joinToString(" ")}")
    val command = subResult.commandSpec().userObject() as BaseHelmCommand
    return command.buildHelmCommandLine()
}

/** Asserts that [list] contains the option [name] immediately followed by [value]. */
fun assertHasOption(list: List<String>, name: String, value: String) {
    val idx = list.indexOf(name)
    Assertions.assertTrue(idx >= 0, "Expected option '$name' in: $list")
    Assertions.assertTrue(idx + 1 < list.size && list[idx + 1] == value, "Expected '$name $value' in: $list")
}

/** Asserts that [list] contains the boolean flag [name]. */
fun assertHasFlag(list: List<String>, name: String) {
    Assertions.assertTrue(list.contains(name), "Expected flag '$name' in: $list")
}

/** Asserts that [list] does not contain [name]. */
fun assertMissing(list: List<String>, name: String) {
    Assertions.assertFalse(list.contains(name), "Did not expect '$name' in: $list")
}
