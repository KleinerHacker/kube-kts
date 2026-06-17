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

import org.pcsoft.framework.kube.kts.cli.commands.*
import org.pcsoft.framework.kube.kts.cli.intern.utils.HELM_MARKER
import picocli.CommandLine.*

/**
 * Main command for the Kube KTS CLI tool.
 *
 * This command serves as the entry point for managing Kubernetes deployments using Kotlin-based chart repositories.
 * It provides access to various subcommands for validating, compiling, rendering, and interacting with Helm charts.
 *
 * The main command includes global flags that can be used across all subcommands, such as debug mode,
 * verbose output, and experimental features. These flags are grouped into default and experimental categories.
 */
@Command(
    subcommands = [
        HelpCommand::class,
        ValidateCommand::class,
        CompileCommand::class,
        RenderCommand::class,
        LintCommand::class,
        TemplateCommand::class,
        InstallCommand::class,
        UninstallCommand::class
    ],
    header = ["Kube KTS 0.1.0 - 2026", ""],
    description = [
        "A Helm wrapper for managing Kubernetes deployments using Kotlin-based chart repositories.",
        "Compiles, renders, and executes Kotlin Script (KTS) Helm charts with full Helm compatibility.",
        ""
    ],
    version = ["Kube KTS 0.1.0"],
    subcommandsRepeatable = false,
    commandListHeading = "%n@|bold Commands|@%n",
    footerHeading = "%n@|bold Option Legend|@%n",
    footer = [
        "  $HELM_MARKER        forwarded to the underlying Helm CLI",
    ],
)
object MainCommand {
    @Mixin
    lateinit var globalFlags: GlobalFlags
}