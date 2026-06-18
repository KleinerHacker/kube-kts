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
 * Group command bundling the `search …` sub-commands that search for charts.
 *
 * Mirrors `helm search`; the actual work is done by the leaf commands. Invoking `search` without a
 * sub-command prints the usage listing.
 */
@Command(
    name = "search",
    description = ["Search for a keyword in charts with helm"],
    subcommands = [
        SearchRepoCommand::class,
        SearchHubCommand::class,
    ],
)
class SearchCommand : BaseGroupCommand()
