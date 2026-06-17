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
import org.pcsoft.framework.kube.kts.cli.intern.*

/**
 * Verifies that every supported Helm flag of each Helm based command is translated and forwarded to
 * the Helm command line. No real Helm process is ever started — only the argument list built by
 * [org.pcsoft.framework.kube.kts.cli.commands.BaseHelmCommand.buildHelmCommandLine] is inspected.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HelmArgsTest {

    // ---------------------------------------------------------------------------------------------
    // install
    // ---------------------------------------------------------------------------------------------

    @Test
    fun install_subcommandAndChartPath() {
        val args = buildHelmCommandLine("install", TEST_REPOSITORY)
        Assertions.assertEquals("install", args.first())
        Assertions.assertEquals(".", args.last { it == "." })
    }

    @Test
    fun install_namePassedAsPositional() {
        val args = buildHelmCommandLine("install", TEST_REPOSITORY, "--name", "my-release")
        Assertions.assertEquals(listOf("install", "my-release", "."), args)
    }

    @Test
    fun install_allFlagsForwarded() {
        val args = buildHelmCommandLine(
            "install", TEST_REPOSITORY, "--name", "rel",
            // global
            "-n", "ns", "--kube-context", "ctx", "--kubeconfig", "kc",
            "--kube-as-group", "g1", "--kube-as-group", "g2",
            "--kube-insecure-skip-tls-verify", "--burst-limit", "120", "--qps", "50",
            // values
            "--set", "a=1", "--set", "b=2", "--set-string", "s=x", "--set-file", "f=p",
            "--set-json", "j={}", "--set-literal", "l=1",
            // chart source
            "--repo", "https://r", "--username", "u", "--password", "p", "--pass-credentials",
            "--version", "1.2.3", "--devel", "--dependency-update", "--verify", "--keyring", "kr",
            // render shared
            "--no-hooks", "--disable-openapi-validation", "--name-template", "nt",
            "--render-subchart-notes", "--skip-crds", "--post-renderer", "pr",
            "--post-renderer-args", "x", "--timeout", "5m",
            // install specific
            "--atomic", "--create-namespace", "--dry-run", "--enable-dns", "--force",
            "--generate-name", "--labels", "k=v", "--description", "desc", "--output", "json",
            "--replace", "--wait", "--wait-for-jobs",
        )

        assertHasOption(args, "--namespace", "ns")
        assertHasOption(args, "--kube-context", "ctx")
        assertHasOption(args, "--kubeconfig", "kc")
        assertHasOption(args, "--kube-as-group", "g1")
        Assertions.assertEquals(2, args.count { it == "--kube-as-group" })
        assertHasFlag(args, "--kube-insecure-skip-tls-verify")
        assertHasOption(args, "--burst-limit", "120")
        assertHasOption(args, "--qps", "50")

        Assertions.assertEquals(2, args.count { it == "--set" })
        assertHasOption(args, "--set-string", "s=x")
        assertHasOption(args, "--set-file", "f=p")
        assertHasOption(args, "--set-json", "j={}")
        assertHasOption(args, "--set-literal", "l=1")

        assertHasOption(args, "--repo", "https://r")
        assertHasOption(args, "--username", "u")
        assertHasOption(args, "--password", "p")
        assertHasFlag(args, "--pass-credentials")
        assertHasOption(args, "--version", "1.2.3")
        assertHasFlag(args, "--devel")
        assertHasFlag(args, "--dependency-update")
        assertHasFlag(args, "--verify")
        assertHasOption(args, "--keyring", "kr")

        assertHasFlag(args, "--no-hooks")
        assertHasFlag(args, "--disable-openapi-validation")
        assertHasOption(args, "--name-template", "nt")
        assertHasFlag(args, "--render-subchart-notes")
        assertHasFlag(args, "--skip-crds")
        assertHasOption(args, "--post-renderer", "pr")
        assertHasOption(args, "--post-renderer-args", "x")
        assertHasOption(args, "--timeout", "5m")

        assertHasFlag(args, "--atomic")
        assertHasFlag(args, "--create-namespace")
        assertHasFlag(args, "--dry-run")
        assertHasFlag(args, "--enable-dns")
        assertHasFlag(args, "--force")
        assertHasFlag(args, "--generate-name")
        assertHasOption(args, "--labels", "k=v")
        assertHasOption(args, "--description", "desc")
        assertHasOption(args, "--output", "json")
        assertHasFlag(args, "--replace")
        assertHasFlag(args, "--wait")
        assertHasFlag(args, "--wait-for-jobs")
    }

    @Test
    fun install_unsetFlagsAreNotForwarded() {
        val args = buildHelmCommandLine("install", TEST_REPOSITORY, "--name", "rel")
        assertMissing(args, "--atomic")
        assertMissing(args, "--dry-run")
        assertMissing(args, "--namespace")
        assertMissing(args, "--set")
        assertMissing(args, "--debug")
    }

    // ---------------------------------------------------------------------------------------------
    // uninstall
    // ---------------------------------------------------------------------------------------------

    @Test
    fun uninstall_releasesAndFlags() {
        val args = buildHelmCommandLine(
            "uninstall", TEST_REPOSITORY, "--name", "rel1", "--name", "rel2",
            "-n", "ns", "--cascade", "foreground", "--description", "d", "--dry-run",
            "--ignore-not-found", "--keep-history", "--no-hooks", "--timeout", "1m", "--wait",
        )
        Assertions.assertEquals("uninstall", args.first())
        Assertions.assertTrue(args.containsAll(listOf("rel1", "rel2")))
        assertHasOption(args, "--namespace", "ns")
        assertHasOption(args, "--cascade", "foreground")
        assertHasOption(args, "--description", "d")
        assertHasFlag(args, "--dry-run")
        assertHasFlag(args, "--ignore-not-found")
        assertHasFlag(args, "--keep-history")
        assertHasFlag(args, "--no-hooks")
        assertHasOption(args, "--timeout", "1m")
        assertHasFlag(args, "--wait")
    }

    // ---------------------------------------------------------------------------------------------
    // lint
    // ---------------------------------------------------------------------------------------------

    @Test
    fun lint_subcommandAndFlags() {
        val args = buildHelmCommandLine(
            "lint", TEST_REPOSITORY, "-n", "ns", "--set", "a=1",
            "--quiet", "--strict", "--with-subcharts",
        )
        Assertions.assertEquals(listOf("lint", "."), args.subList(0, 2))
        assertHasOption(args, "--namespace", "ns")
        assertHasOption(args, "--set", "a=1")
        assertHasFlag(args, "--quiet")
        assertHasFlag(args, "--strict")
        assertHasFlag(args, "--with-subcharts")
    }

    // ---------------------------------------------------------------------------------------------
    // template
    // ---------------------------------------------------------------------------------------------

    @Test
    fun template_namePositionalAndNamespaceShorthand() {
        val args = buildHelmCommandLine("template", TEST_REPOSITORY, "--name", "rel", "-n", "ns")
        Assertions.assertEquals(listOf("template", "rel", "."), args.subList(0, 3))
        // -n must become --namespace (forwarded), not be consumed as the release name
        assertHasOption(args, "--namespace", "ns")
    }

    @Test
    fun template_allFlagsForwarded() {
        val args = buildHelmCommandLine(
            "template", TEST_REPOSITORY, "--name", "rel",
            "-a", "v1", "-a", "v2", "--include-crds", "--is-upgrade", "--kube-version", "1.29",
            "--output-dir", "out", "-s", "tpl1", "-s", "tpl2", "--skip-tests", "--validate",
            "--dry-run", "--generate-name", "--labels", "k=v", "--create-namespace",
        )
        Assertions.assertEquals(2, args.count { it == "--api-versions" })
        assertHasFlag(args, "--include-crds")
        assertHasFlag(args, "--is-upgrade")
        assertHasOption(args, "--kube-version", "1.29")
        assertHasOption(args, "--output-dir", "out")
        Assertions.assertEquals(2, args.count { it == "--show-only" })
        assertHasFlag(args, "--skip-tests")
        assertHasFlag(args, "--validate")
        assertHasFlag(args, "--dry-run")
        assertHasFlag(args, "--generate-name")
        assertHasOption(args, "--labels", "k=v")
        assertHasFlag(args, "--create-namespace")
    }

    // ---------------------------------------------------------------------------------------------
    // shared: values files and debug forwarding
    // ---------------------------------------------------------------------------------------------

    @Test
    fun valuesFilesForwardedAsDashF() {
        val args = buildHelmCommandLine("template", TEST_REPOSITORY, "--name", "rel", "-f", TEST_VALUES_FILE)
        assertHasOption(args, "-f", TEST_VALUES_FILE)
    }

    @Test
    fun debugFlagForwardedToHelm() {
        val args = buildHelmCommandLine("--debug", "lint", TEST_REPOSITORY)
        assertHasFlag(args, "--debug")
    }
}
