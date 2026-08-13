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
 * [org.pcsoft.framework.kube.kts.cli.commands.BaseRenderedHelmCommand.buildHelmCommandLine] is inspected.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HelmArgsTest {

    // ---------------------------------------------------------------------------------------------
    // install
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `install` is translated into the Helm sub-command with `.` as the chart path.
     *
     * The rendered chart is always passed as the current directory, because Helm is executed inside the render target.
     */
    @Test
    fun install_subcommandAndChartPath() {
        val args = buildHelmCommandLine("install", TEST_REPOSITORY)
        Assertions.assertEquals("install", args.first())
        Assertions.assertEquals(".", args.last { it == "." })
    }

    /**
     * Verifies that the release name given via `--name` becomes a positional argument.
     *
     * Helm expects `install <release> <chart>`, so the option must not be forwarded as an option.
     */
    @Test
    fun install_namePassedAsPositional() {
        val args = buildHelmCommandLine("install", TEST_REPOSITORY, "--name", "my-release")
        Assertions.assertEquals(listOf("install", "my-release", "."), args)
    }

    /**
     * Verifies that every supported `install` flag is forwarded to Helm.
     *
     * Covers the global flags, the value flags, the chart source and verification flags as well as the install specific flags in a single command line.
     */
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

    /**
     * Verifies that flags which were not given do not appear on the Helm command line.
     *
     * Forwarding a defaulted boolean as `--flag=false` would change Helm's behaviour, so unset options must be omitted entirely.
     */
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

    /**
     * Verifies that several releases and the uninstall flags are forwarded to Helm.
     *
     * `helm uninstall` accepts multiple releases in one call; the repeated `--name` options must become positional arguments in order.
     */
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
    // status (render-less: no REPOSITORY positional, forwarded directly to helm)
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the release of the direct command `status` is forwarded as a positional argument.
     *
     * Direct commands take the release name as a plain positional, unlike the render based commands which use `--name`.
     */
    @Test
    fun status_releasePassedAsPositional() {
        val args = buildHelmCommandLine("status", "my-release")
        Assertions.assertEquals(listOf("status", "my-release"), args)
    }

    /**
     * Verifies that the `status` flags are forwarded to Helm.
     *
     * Covers `--revision`, `--show-resources`, the output format and the global flags.
     */
    @Test
    fun status_flagsForwarded() {
        val args = buildHelmCommandLine(
            "status", "rel", "-n", "ns", "--revision", "3", "--output", "json",
            "--show-desc", "--show-resources",
        )
        Assertions.assertEquals(listOf("status", "rel"), args.subList(0, 2))
        assertHasOption(args, "--namespace", "ns")
        assertHasOption(args, "--revision", "3")
        assertHasOption(args, "--output", "json")
        assertHasFlag(args, "--show-desc")
        assertHasFlag(args, "--show-resources")
    }

    /**
     * Verifies that unset `status` flags do not appear on the Helm command line.
     *
     * Only options explicitly given by the user may be forwarded.
     */
    @Test
    fun status_unsetFlagsAreNotForwarded() {
        val args = buildHelmCommandLine("status", "rel")
        assertMissing(args, "--revision")
        assertMissing(args, "--output")
        assertMissing(args, "--show-desc")
        assertMissing(args, "--namespace")
        assertMissing(args, "--debug")
    }

    // ---------------------------------------------------------------------------------------------
    // lint
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `lint` is translated into `helm lint .` including its flags.
     *
     * Covers `--strict`, `--with-subcharts`, the value flags and the global flags.
     */
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

    /**
     * Verifies that `template` passes the release name positionally and expands `-n`.
     *
     * `-n` is reserved for `--namespace` to match Helm, while the release name is given via `--name`.
     */
    @Test
    fun template_namePositionalAndNamespaceShorthand() {
        val args = buildHelmCommandLine("template", TEST_REPOSITORY, "--name", "rel", "-n", "ns")
        Assertions.assertEquals(listOf("template", "rel", "."), args.subList(0, 3))
        // -n must become --namespace (forwarded), not be consumed as the release name
        assertHasOption(args, "--namespace", "ns")
    }

    /**
     * Verifies that every supported `template` flag is forwarded to Helm.
     *
     * Covers the rendering flags such as `--include-crds`, `--show-only` and `--output-dir` together with the value and global flags.
     */
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
    // list (render-less)
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `list` is translated into `helm list` including all its filter flags.
     *
     * Covers the status filters, `--all-namespaces`, the selector, the output format and the pagination options.
     */
    @Test
    fun list_subcommandAndFlags() {
        val args = buildHelmCommandLine(
            "list", "-n", "ns", "--all", "--all-namespaces", "--date", "--deployed", "--failed",
            "--filter", "re", "--max", "5", "--no-headers", "--offset", "2", "--output", "json",
            "--pending", "--reverse", "--selector", "k=v", "--short", "--superseded",
            "--time-format", "2006", "--uninstalled", "--uninstalling",
        )
        Assertions.assertEquals("list", args.first())
        assertHasOption(args, "--namespace", "ns")
        assertHasFlag(args, "--all")
        assertHasFlag(args, "--all-namespaces")
        assertHasFlag(args, "--date")
        assertHasFlag(args, "--deployed")
        assertHasFlag(args, "--failed")
        assertHasOption(args, "--filter", "re")
        assertHasOption(args, "--max", "5")
        assertHasFlag(args, "--no-headers")
        assertHasOption(args, "--offset", "2")
        assertHasOption(args, "--output", "json")
        assertHasFlag(args, "--pending")
        assertHasFlag(args, "--reverse")
        assertHasOption(args, "--selector", "k=v")
        assertHasFlag(args, "--short")
        assertHasFlag(args, "--superseded")
        assertHasOption(args, "--time-format", "2006")
        assertHasFlag(args, "--uninstalled")
        assertHasFlag(args, "--uninstalling")
    }

    // ---------------------------------------------------------------------------------------------
    // history
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `history` forwards the release, `--max` and the output format.
     *
     * The release name is a positional argument of the direct command.
     */
    @Test
    fun history_releaseAndFlags() {
        val args = buildHelmCommandLine("history", "rel", "--max", "3", "-o", "json")
        Assertions.assertEquals(listOf("history", "rel"), args.subList(0, 2))
        assertHasOption(args, "--max", "3")
        assertHasOption(args, "--output", "json")
    }

    // ---------------------------------------------------------------------------------------------
    // rollback
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `rollback` forwards release, revision and all rollback flags.
     *
     * Covers `--force`, `--wait`, `--cleanup-on-fail` and the timeout; release and revision must keep their positional order.
     */
    @Test
    fun rollback_releaseRevisionAndFlags() {
        val args = buildHelmCommandLine(
            "rollback", "rel", "2", "--cleanup-on-fail", "--dry-run", "--force",
            "--history-max", "5", "--no-hooks", "--recreate-pods", "--timeout", "1m",
            "--wait", "--wait-for-jobs",
        )
        Assertions.assertEquals(listOf("rollback", "rel", "2"), args.subList(0, 3))
        assertHasFlag(args, "--cleanup-on-fail")
        assertHasFlag(args, "--dry-run")
        assertHasFlag(args, "--force")
        assertHasOption(args, "--history-max", "5")
        assertHasFlag(args, "--no-hooks")
        assertHasFlag(args, "--recreate-pods")
        assertHasOption(args, "--timeout", "1m")
        assertHasFlag(args, "--wait")
        assertHasFlag(args, "--wait-for-jobs")
    }

    /**
     * Verifies that the optional revision may be omitted for `rollback`.
     *
     * Without a revision Helm rolls back to the previous release, so only the release name is forwarded.
     */
    @Test
    fun rollback_revisionOptional() {
        val args = buildHelmCommandLine("rollback", "rel")
        Assertions.assertEquals(listOf("rollback", "rel"), args)
    }

    // ---------------------------------------------------------------------------------------------
    // test
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `test` forwards the release together with `--filter`, `--logs` and the timeout.
     *
     * `test` runs the Helm test hooks of an installed release and needs no repository.
     */
    @Test
    fun test_releaseAndFlags() {
        val args = buildHelmCommandLine(
            "test", "rel", "--filter", "name=a", "--filter", "name=b",
            "--hide-notes", "--logs", "--timeout", "2m",
        )
        Assertions.assertEquals(listOf("test", "rel"), args.subList(0, 2))
        Assertions.assertEquals(2, args.count { it == "--filter" })
        assertHasFlag(args, "--hide-notes")
        assertHasFlag(args, "--logs")
        assertHasOption(args, "--timeout", "2m")
    }

    // ---------------------------------------------------------------------------------------------
    // pull
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `pull` forwards the chart reference and all download flags.
     *
     * Covers `--destination`, `--untar`, `--prov`, the version and the repository/verification flags.
     */
    @Test
    fun pull_chartAndFlags() {
        val args = buildHelmCommandLine(
            "pull", "bitnami/nginx", "-d", "out", "--prov", "--untar", "--untardir", "ud",
            "--repo", "https://r", "--username", "u", "--password", "p", "--verify",
            "--keyring", "kr", "--version", "1.2.3", "--devel", "--plain-http",
        )
        Assertions.assertEquals(listOf("pull", "bitnami/nginx"), args.subList(0, 2))
        assertHasOption(args, "--destination", "out")
        assertHasFlag(args, "--prov")
        assertHasFlag(args, "--untar")
        assertHasOption(args, "--untardir", "ud")
        assertHasOption(args, "--repo", "https://r")
        assertHasOption(args, "--username", "u")
        assertHasOption(args, "--password", "p")
        assertHasFlag(args, "--verify")
        assertHasOption(args, "--keyring", "kr")
        assertHasOption(args, "--version", "1.2.3")
        assertHasFlag(args, "--devel")
        assertHasFlag(args, "--plain-http")
    }

    // ---------------------------------------------------------------------------------------------
    // push
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `push` forwards the chart archive, the remote and the registry flags.
     *
     * Chart and remote are positional arguments and must keep their order.
     */
    @Test
    fun push_chartRemoteAndFlags() {
        val args = buildHelmCommandLine(
            "push", "chart.tgz", "oci://r", "--ca-file", "ca", "--cert-file", "cert",
            "--key-file", "key", "--insecure-skip-tls-verify", "--plain-http",
        )
        Assertions.assertEquals(listOf("push", "chart.tgz", "oci://r"), args.subList(0, 3))
        assertHasOption(args, "--ca-file", "ca")
        assertHasOption(args, "--cert-file", "cert")
        assertHasOption(args, "--key-file", "key")
        assertHasFlag(args, "--insecure-skip-tls-verify")
        assertHasFlag(args, "--plain-http")
    }

    // ---------------------------------------------------------------------------------------------
    // verify / version / env
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `verify` forwards the chart path and `--keyring`.
     *
     * The keyring decides which public keys are used to check the provenance file.
     */
    @Test
    fun verify_pathAndKeyring() {
        val args = buildHelmCommandLine("verify", "chart.tgz", "--keyring", "kr")
        Assertions.assertEquals(listOf("verify", "chart.tgz"), args.subList(0, 2))
        assertHasOption(args, "--keyring", "kr")
    }

    /**
     * Verifies that `version` forwards `--short` and `--template`.
     *
     * Both flags only change how Helm formats its own version output.
     */
    @Test
    fun version_flags() {
        val args = buildHelmCommandLine("version", "--short", "--template", "{{.Version}}")
        Assertions.assertEquals("version", args.first())
        assertHasFlag(args, "--short")
        assertHasOption(args, "--template", "{{.Version}}")
    }

    /**
     * Verifies that `env` works with and without an environment variable name.
     *
     * Without a name Helm prints all variables; with a name only that single value.
     */
    @Test
    fun env_optionalName() {
        Assertions.assertEquals(listOf("env"), buildHelmCommandLine("env"))
        Assertions.assertEquals(listOf("env", "HELM_BIN"), buildHelmCommandLine("env", "HELM_BIN"))
    }

    // ---------------------------------------------------------------------------------------------
    // get (nested)
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the nested command `get all` is flattened and forwarded.
     *
     * Nested picocli commands must be resolved down to the leaf before the Helm command line is built.
     */
    @Test
    fun get_all() {
        val args = buildHelmCommandLine("get", "all", "rel", "--revision", "2", "--template", "t", "-o", "json")
        Assertions.assertEquals(listOf("get", "all", "rel"), args.subList(0, 3))
        assertHasOption(args, "--revision", "2")
        assertHasOption(args, "--template", "t")
        assertHasOption(args, "--output", "json")
    }

    /**
     * Verifies that `get values` forwards `--all`, the output format and the JSONPath option.
     *
     * `get values` is the only `get` variant that additionally supports `--jsonpath`.
     */
    @Test
    fun get_values() {
        val args = buildHelmCommandLine("get", "values", "rel", "-a", "--revision", "1", "-o", "yaml")
        Assertions.assertEquals(listOf("get", "values", "rel"), args.subList(0, 3))
        assertHasFlag(args, "--all")
        assertHasOption(args, "--revision", "1")
        assertHasOption(args, "--output", "yaml")
    }

    /**
     * Verifies that the remaining `get` variants are forwarded correctly.
     *
     * Covers `manifest`, `hooks`, `notes` and `metadata` in one test, since they share the same option set.
     */
    @Test
    fun get_manifestHooksNotesMetadata() {
        Assertions.assertEquals(listOf("get", "manifest", "rel"), buildHelmCommandLine("get", "manifest", "rel"))
        Assertions.assertEquals(listOf("get", "hooks", "rel"), buildHelmCommandLine("get", "hooks", "rel"))
        Assertions.assertEquals(listOf("get", "notes", "rel"), buildHelmCommandLine("get", "notes", "rel"))
        val meta = buildHelmCommandLine("get", "metadata", "rel", "-o", "json")
        Assertions.assertEquals(listOf("get", "metadata", "rel"), meta.subList(0, 3))
        assertHasOption(meta, "--output", "json")
    }

    // ---------------------------------------------------------------------------------------------
    // repo (nested)
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `repo add` forwards name, URL and all authentication and TLS flags.
     *
     * Covers `--username`, `--password`, the certificate options and `--force-update`.
     */
    @Test
    fun repo_add() {
        val args = buildHelmCommandLine(
            "repo", "add", "bitnami", "https://charts.bitnami.com/bitnami",
            "--username", "u", "--password", "p", "--pass-credentials", "--ca-file", "ca",
            "--cert-file", "cert", "--key-file", "key", "--insecure-skip-tls-verify",
            "--no-update", "--force-update", "--allow-deprecated-repos",
        )
        Assertions.assertEquals(listOf("repo", "add", "bitnami", "https://charts.bitnami.com/bitnami"), args.subList(0, 4))
        assertHasOption(args, "--username", "u")
        assertHasOption(args, "--password", "p")
        assertHasFlag(args, "--pass-credentials")
        assertHasOption(args, "--ca-file", "ca")
        assertHasOption(args, "--cert-file", "cert")
        assertHasOption(args, "--key-file", "key")
        assertHasFlag(args, "--insecure-skip-tls-verify")
        assertHasFlag(args, "--no-update")
        assertHasFlag(args, "--force-update")
        assertHasFlag(args, "--allow-deprecated-repos")
    }

    /**
     * Verifies that the remaining `repo` sub-commands and their aliases are forwarded.
     *
     * Covers `update`/`up`, `list`/`ls` and `remove`/`rm`, which must all map to the long Helm sub-command.
     */
    @Test
    fun repo_updateListRemove() {
        val up = buildHelmCommandLine("repo", "update", "r1", "r2", "--fail-on-repo-update-fail")
        Assertions.assertEquals(listOf("repo", "update", "r1", "r2"), up.subList(0, 4))
        assertHasFlag(up, "--fail-on-repo-update-fail")
        Assertions.assertEquals(listOf("repo", "update"), buildHelmCommandLine("repo", "update"))

        val list = buildHelmCommandLine("repo", "list", "-o", "json")
        Assertions.assertEquals(listOf("repo", "list"), list.subList(0, 2))
        assertHasOption(list, "--output", "json")

        Assertions.assertEquals(listOf("repo", "remove", "r1", "r2"), buildHelmCommandLine("repo", "remove", "r1", "r2"))
    }

    // ---------------------------------------------------------------------------------------------
    // search (nested)
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `search repo` forwards the keyword and its search flags.
     *
     * Covers `--regexp`, `--versions`, `--devel` and the output format.
     */
    @Test
    fun search_repo() {
        val args = buildHelmCommandLine(
            "search", "repo", "nginx", "--devel", "--fail-on-no-result", "--max-col-width", "80",
            "-o", "json", "-r", "--version", "1.0", "-l",
        )
        Assertions.assertEquals(listOf("search", "repo", "nginx"), args.subList(0, 3))
        assertHasFlag(args, "--devel")
        assertHasFlag(args, "--fail-on-no-result")
        assertHasOption(args, "--max-col-width", "80")
        assertHasOption(args, "--output", "json")
        assertHasFlag(args, "--regexp")
        assertHasOption(args, "--version", "1.0")
        assertHasFlag(args, "--versions")
    }

    /**
     * Verifies that `search hub` forwards the keyword, `--endpoint` and `--max-col-width`.
     *
     * `search hub` queries the Artifact Hub instead of the locally configured repositories.
     */
    @Test
    fun search_hub() {
        val args = buildHelmCommandLine(
            "search", "hub", "nginx", "--endpoint", "https://hub", "--fail-on-no-result",
            "--list-repo-url", "--max-col-width", "60", "-o", "yaml",
        )
        Assertions.assertEquals(listOf("search", "hub", "nginx"), args.subList(0, 3))
        assertHasOption(args, "--endpoint", "https://hub")
        assertHasFlag(args, "--fail-on-no-result")
        assertHasFlag(args, "--list-repo-url")
        assertHasOption(args, "--max-col-width", "60")
        assertHasOption(args, "--output", "yaml")
    }

    // ---------------------------------------------------------------------------------------------
    // registry (nested)
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `registry login` and `registry logout` are forwarded with their flags.
     *
     * Covers the credentials, `--password-stdin` and the TLS options of the OCI registry.
     */
    @Test
    fun registry_loginLogout() {
        val login = buildHelmCommandLine(
            "registry", "login", "registry.example.com", "-u", "u", "-p", "p",
            "--password-stdin", "--insecure", "--ca-file", "ca", "--cert-file", "cert",
            "--key-file", "key", "--plain-http",
        )
        Assertions.assertEquals(listOf("registry", "login", "registry.example.com"), login.subList(0, 3))
        assertHasOption(login, "--username", "u")
        assertHasOption(login, "--password", "p")
        assertHasFlag(login, "--password-stdin")
        assertHasFlag(login, "--insecure")
        assertHasOption(login, "--ca-file", "ca")
        assertHasOption(login, "--cert-file", "cert")
        assertHasOption(login, "--key-file", "key")
        assertHasFlag(login, "--plain-http")

        Assertions.assertEquals(
            listOf("registry", "logout", "registry.example.com"),
            buildHelmCommandLine("registry", "logout", "registry.example.com"),
        )
    }

    // ---------------------------------------------------------------------------------------------
    // show (nested)
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that all `show` variants and the `inspect` alias are forwarded.
     *
     * Covers `all`, `chart`, `values`, `readme` and `crds` including the chart source flags.
     */
    @Test
    fun show_variants() {
        val all = buildHelmCommandLine("show", "all", "bitnami/nginx", "--version", "1.0", "--repo", "https://r")
        Assertions.assertEquals(listOf("show", "all", "bitnami/nginx"), all.subList(0, 3))
        assertHasOption(all, "--version", "1.0")
        assertHasOption(all, "--repo", "https://r")

        Assertions.assertEquals(listOf("show", "chart", "c"), buildHelmCommandLine("show", "chart", "c"))
        Assertions.assertEquals(listOf("show", "readme", "c"), buildHelmCommandLine("show", "readme", "c"))
        Assertions.assertEquals(listOf("show", "crds", "c"), buildHelmCommandLine("show", "crds", "c"))

        val values = buildHelmCommandLine("show", "values", "c", "--jsonpath", "{.image}")
        Assertions.assertEquals(listOf("show", "values", "c"), values.subList(0, 3))
        assertHasOption(values, "--jsonpath", "{.image}")
    }

    // ---------------------------------------------------------------------------------------------
    // package (render based)
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `package` is translated into `helm package .` including its flags.
     *
     * Covers `--version`, `--app-version`, `--destination`, `--dependency-update` and the signing options.
     */
    @Test
    fun package_subcommandAndFlags() {
        val args = buildHelmCommandLine(
            "package", TEST_REPOSITORY, "--app-version", "1.0", "--version", "2.0",
            "-d", "out", "-u", "--sign", "--key", "k", "--keyring", "kr", "--pass-stdin",
        )
        Assertions.assertEquals(listOf("package", "."), args.subList(0, 2))
        assertHasOption(args, "--app-version", "1.0")
        assertHasOption(args, "--version", "2.0")
        assertHasOption(args, "--destination", "out")
        assertHasFlag(args, "--dependency-update")
        assertHasFlag(args, "--sign")
        assertHasOption(args, "--key", "k")
        assertHasOption(args, "--keyring", "kr")
        assertHasFlag(args, "--pass-stdin")
    }

    // ---------------------------------------------------------------------------------------------
    // dependency (nested, render based)
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the `dependency` sub-commands and their aliases are forwarded.
     *
     * Covers `build`, `update`/`up` and `list`/`ls`, each operating on the rendered chart `.`.
     */
    @Test
    fun dependency_buildUpdateList() {
        val build = buildHelmCommandLine("dependency", "build", TEST_REPOSITORY, "--keyring", "kr", "--skip-refresh", "--verify")
        Assertions.assertEquals(listOf("dependency", "build", "."), build.subList(0, 3))
        assertHasOption(build, "--keyring", "kr")
        assertHasFlag(build, "--skip-refresh")
        assertHasFlag(build, "--verify")

        val update = buildHelmCommandLine("dependency", "update", TEST_REPOSITORY)
        Assertions.assertEquals(listOf("dependency", "update", "."), update.subList(0, 3))

        val list = buildHelmCommandLine("dependency", "list", TEST_REPOSITORY, "--max-col-width", "80")
        Assertions.assertEquals(listOf("dependency", "list", "."), list.subList(0, 3))
        assertHasOption(list, "--max-col-width", "80")
    }

    // ---------------------------------------------------------------------------------------------
    // diff (nested, render based, plugin)
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that `diff upgrade` is forwarded to the helm-diff plugin.
     *
     * The release name given via `--name` becomes the positional RELEASE, followed by the rendered chart `.`.
     */
    @Test
    fun diff_upgrade() {
        val args = buildHelmCommandLine(
            "diff", "upgrade", TEST_REPOSITORY, "--name", "rel", "--set", "a=1",
            "--detailed-exitcode", "--context", "3", "--show-secrets", "--no-hooks",
            "--include-tests", "--reset-values", "--reuse-values", "--normalize-manifests",
        )
        Assertions.assertEquals(listOf("diff", "upgrade", "rel", "."), args.subList(0, 4))
        assertHasOption(args, "--set", "a=1")
        assertHasFlag(args, "--detailed-exitcode")
        assertHasOption(args, "--context", "3")
        assertHasFlag(args, "--show-secrets")
        assertHasFlag(args, "--no-hooks")
        assertHasFlag(args, "--include-tests")
        assertHasFlag(args, "--reset-values")
        assertHasFlag(args, "--reuse-values")
        assertHasFlag(args, "--normalize-manifests")
    }

    // ---------------------------------------------------------------------------------------------
    // shared: values files and debug forwarding
    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that values files are forwarded as `-f` and keep their order.
     *
     * Helm applies overlays in the given order, so a reordering would change the effective values.
     */
    @Test
    fun valuesFilesForwardedAsDashF() {
        val args = buildHelmCommandLine("template", TEST_REPOSITORY, "--name", "rel", "-f", TEST_VALUES_FILE)
        assertHasOption(args, "-f", TEST_VALUES_FILE)
    }

    /**
     * Verifies that the CLI's own `--debug` flag is additionally forwarded to Helm.
     *
     * `--debug` is one of the few own flags that are not consumed by the CLI alone.
     */
    @Test
    fun debugFlagForwardedToHelm() {
        val args = buildHelmCommandLine("--debug", "lint", TEST_REPOSITORY)
        assertHasFlag(args, "--debug")
    }
}
