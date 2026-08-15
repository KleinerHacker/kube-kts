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

package org.pcsoft.framework.kube.kts.cli

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.cli.intern.utils.DANGER_MARKER
import org.pcsoft.framework.kube.kts.cli.intern.utils.EXPERIMENTAL_MARKER
import org.pcsoft.framework.kube.kts.cli.intern.utils.HELM_MARKER
import org.pcsoft.framework.kube.kts.cli.intern.utils.HelmHelpFactory
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Verifies the rendered help page of each command: forwarded options must carry the dedicated
 * `→helm` column, internal-only options must not, and the raw marker must never leak into the
 * description text.
 */
class HelpRenderTest {

    /** The hint rendered in the dedicated Helm column, derived from [HELM_MARKER] without ANSI styling. */
    private val helmHint = CommandLine.Help.Ansi.OFF.string(HELM_MARKER)

    /** The hint rendered for experimental options, derived from [EXPERIMENTAL_MARKER] without styling. */
    private val experimentalHint = CommandLine.Help.Ansi.OFF.string(EXPERIMENTAL_MARKER)

    /** The hint rendered for dangerous options, derived from [DANGER_MARKER] without styling. */
    private val dangerHint = CommandLine.Help.Ansi.OFF.string(DANGER_MARKER)

    private fun usageOf(vararg path: String): String {
        val cli = CommandLine(MainCommand).apply {
            helpFactory = HelmHelpFactory()
            usageHelpWidth = 120
            usageHelpLongOptionsMaxWidth = 80
        }
        var current = cli
        for (name in path) current = current.subcommands[name]!!
        val writer = StringWriter()
        current.usage(PrintWriter(writer), CommandLine.Help.Ansi.OFF)
        return writer.toString()
    }

    /** Returns the option row (the line introducing [option]). */
    private fun rowOf(usage: String, option: String): String {
        val regex = Regex("(^|\\s)${Regex.escape(option)}(\\s|=|$)")
        return usage.lineSequence().firstOrNull { regex.containsMatchIn(it) }
            ?: Assertions.fail("Option '$option' not found in usage:\n$usage")
    }

    /** Asserts the option is shown with the Helm column, placed between the option and its description. */
    private fun assertForwarded(usage: String, option: String) {
        val row = rowOf(usage, option)
        Assertions.assertTrue(row.contains(helmHint), "Expected '$helmHint' column for '$option' in row: $row")
        Assertions.assertTrue(
            row.indexOf(option) < row.indexOf(helmHint),
            "Column order wrong for '$option' in row: $row"
        )
    }

    /** Asserts the option is shown without the Helm column (internal only). */
    private fun assertInternal(usage: String, option: String) {
        val row = rowOf(usage, option)
        Assertions.assertFalse(row.contains(helmHint), "Did not expect '$helmHint' column for '$option' in row: $row")
    }

    /**
     * The marker must appear only in the dedicated column, never additionally in the description.
     * It is therefore allowed at most once per row.
     */
    private fun assertNoRawMarker(usage: String) {
        usage.lineSequence().forEach { line ->
            val count = Regex(Regex.escape(helmHint)).findAll(line).count()
            Assertions.assertTrue(count <= 1, "Marker leaked into description in row: $line")
        }
    }

    /**
     * Verifies that the help of `install` lists its options with the Helm forwarding marker.
     *
     * Every option that is passed on to Helm must be marked with `---->` so users can tell them apart from the CLI's own flags.
     */
    @Test
    fun installHelp() {
        val usage = usageOf("install")
        assertForwarded(usage, "--atomic")
        assertForwarded(usage, "--set")
        assertForwarded(usage, "--namespace")
        assertForwarded(usage, "--values")
        assertInternal(usage, "--name")
        assertInternal(usage, "--exception")
        assertNoRawMarker(usage)
    }

    /**
     * Verifies that the help of `uninstall` renders its options and markers.
     *
     * Includes the repeatable `--name` option used to uninstall several releases at once.
     */
    @Test
    fun uninstallHelp() {
        val usage = usageOf("uninstall")
        assertForwarded(usage, "--cascade")
        assertForwarded(usage, "--keep-history")
        assertForwarded(usage, "--namespace")
        assertInternal(usage, "--name")
        assertInternal(usage, "--verbose")
        assertNoRawMarker(usage)
    }

    /**
     * Verifies that the help of `lint` renders its options and markers.
     *
     * Covers the lint specific flags on top of the shared value and global flags.
     */
    @Test
    fun lintHelp() {
        val usage = usageOf("lint")
        assertForwarded(usage, "--strict")
        assertForwarded(usage, "--quiet")
        assertForwarded(usage, "--set")
        assertForwarded(usage, "--values")
        assertInternal(usage, "--exception")
        assertNoRawMarker(usage)
    }

    /**
     * Verifies that the help of `template` renders its options and markers.
     *
     * Covers the rendering flags that only exist for `template`.
     */
    @Test
    fun templateHelp() {
        val usage = usageOf("template")
        assertForwarded(usage, "--include-crds")
        assertForwarded(usage, "--validate")
        assertForwarded(usage, "--namespace")
        assertForwarded(usage, "--values")
        assertInternal(usage, "--name")
        assertInternal(usage, "--exception")
        assertNoRawMarker(usage)
    }

    /**
     * Verifies that the help of the direct command `list` renders its options and markers.
     *
     * Direct commands take no repository, so no positional repository parameter may be shown.
     */
    @Test
    fun listHelp() {
        val usage = usageOf("list")
        assertForwarded(usage, "--all-namespaces")
        assertForwarded(usage, "--output")
        assertForwarded(usage, "--namespace")
        assertNoRawMarker(usage)
    }

    /**
     * Verifies that the help of `pull` renders its options and markers.
     *
     * Covers the download and verification flags of the chart source.
     */
    @Test
    fun pullHelp() {
        val usage = usageOf("pull")
        assertForwarded(usage, "--destination")
        assertForwarded(usage, "--untar")
        assertForwarded(usage, "--repo")
        assertNoRawMarker(usage)
    }

    /**
     * Verifies that the help of `package` renders its options and markers.
     *
     * Covers the packaging and signing flags.
     */
    @Test
    fun packageHelp() {
        val usage = usageOf("package")
        assertForwarded(usage, "--app-version")
        assertForwarded(usage, "--pass-stdin")
        assertForwarded(usage, "--values")
        assertNoRawMarker(usage)
    }

    /**
     * Verifies that the help of the nested command `get values` is rendered.
     *
     * The usage line must show the full command path, not just the leaf command.
     */
    @Test
    fun nestedGetValuesHelp() {
        val usage = usageOf("get", "values")
        assertForwarded(usage, "--all")
        assertForwarded(usage, "--output")
        assertForwarded(usage, "--namespace")
        assertNoRawMarker(usage)
    }

    /**
     * Verifies that the help of the nested command `diff upgrade` is rendered.
     *
     * Same expectation as for `get values`, but for a render based nested command.
     */
    @Test
    fun nestedDiffUpgradeHelp() {
        val usage = usageOf("diff", "upgrade")
        assertForwarded(usage, "--detailed-exitcode")
        assertForwarded(usage, "--set")
        assertInternal(usage, "--name")
        assertNoRawMarker(usage)
    }

    /**
     * Verifies that experimental options are marked with `*` in the help.
     *
     * The YAML merge flags are only usable together with `--experimental` and must be recognisable as such.
     */
    @Test
    fun experimentalFlagsCarryMarker() {
        val usage = usageOf("install")
        for (option in listOf("--yaml-merge", "--yaml-array-merge")) {
            val row = rowOf(usage, option)
            Assertions.assertTrue(
                row.contains(experimentalHint),
                "Expected '$experimentalHint' column for '$option' in row: $row"
            )
            Assertions.assertTrue(
                row.indexOf(option) < row.indexOf(experimentalHint),
                "Column order wrong for '$option' in row: $row"
            )
            Assertions.assertFalse(
                row.contains(helmHint),
                "Experimental option '$option' must not be marked as forwarded: $row"
            )
        }
    }

    /**
     * Verifies that the security relevant `--unsafe` flag is marked with `!!!` in the help.
     *
     * `--unsafe` disables the script safety model, so its marker must warn the user.
     */
    @Test
    fun dangerousFlagCarriesMarker() {
        val usage = usageOf("install")
        val row = rowOf(usage, "--unsafe")
        Assertions.assertTrue(row.contains(dangerHint), "Expected '$dangerHint' column for '--unsafe' in row: $row")
        Assertions.assertTrue(
            row.indexOf("--unsafe") < row.indexOf(dangerHint),
            "Column order wrong for '--unsafe' in row: $row"
        )
    }

    /**
     * Verifies that `--debug` carries the Helm forwarding marker in the help.
     *
     * The flag is consumed by the CLI and additionally passed on to Helm, which the help must state.
     */
    @Test
    fun debugFlagIsForwardedInHelp() {
        // --debug is used internally and additionally forwarded to Helm.
        val usage = usageOf("install")
        assertForwarded(usage, "--debug")
    }
}
