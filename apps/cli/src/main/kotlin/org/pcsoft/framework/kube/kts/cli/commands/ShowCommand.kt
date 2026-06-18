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
 * Group command bundling the `show …` sub-commands that display information about a chart.
 *
 * Mirrors `helm show` (alias `inspect`); the actual work is done by the leaf commands. Invoking
 * `show` without a sub-command prints the usage listing.
 */
@Command(
    name = "show",
    aliases = ["inspect"],
    description = ["Show information of a chart with helm"],
    subcommands = [
        ShowAllCommand::class,
        ShowChartCommand::class,
        ShowValuesCommand::class,
        ShowReadmeCommand::class,
        ShowCrdsCommand::class,
    ],
)
class ShowCommand : BaseGroupCommand()
