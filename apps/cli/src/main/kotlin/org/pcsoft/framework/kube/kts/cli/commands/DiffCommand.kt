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

package org.pcsoft.framework.kube.kts.cli.commands

import picocli.CommandLine.Command

/**
 * Group command bundling the `diff …` sub-commands that preview changes against the cluster.
 *
 * Mirrors the external `helm diff` plugin; the actual work is done by the leaf commands, which render
 * the KTS repository first and then diff the rendered chart. Invoking `diff` without a sub-command
 * prints the usage listing.
 *
 * Note: requires the external `helm-diff` plugin to be installed (`helm plugin install
 * https://github.com/databus23/helm-diff`).
 */
@Command(
    name = "diff",
    description = ["Preview changes of a KTS based chart repository with the helm-diff plugin"],
    subcommands = [
        DiffUpgradeCommand::class,
    ],
)
class DiffCommand : BaseGroupCommand()
