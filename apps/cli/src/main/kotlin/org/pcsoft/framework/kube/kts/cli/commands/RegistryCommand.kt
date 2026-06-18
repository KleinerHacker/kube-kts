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
 * Group command bundling the `registry …` sub-commands that manage login to OCI registries.
 *
 * Mirrors `helm registry`; the actual work is done by the leaf commands. Invoking `registry` without
 * a sub-command prints the usage listing.
 */
@Command(
    name = "registry",
    description = ["Login to or logout from a registry with helm"],
    subcommands = [
        RegistryLoginCommand::class,
        RegistryLogoutCommand::class,
    ],
)
class RegistryCommand : BaseGroupCommand()
