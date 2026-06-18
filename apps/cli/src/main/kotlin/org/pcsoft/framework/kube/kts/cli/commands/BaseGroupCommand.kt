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

import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Spec

/**
 * Base class for grouping commands that only bundle sub-commands and never invoke Helm themselves
 * (e.g. `get`, `repo`, `show`).
 *
 * Mirrors Helm's command groups: invoking the group without a sub-command simply prints its usage
 * listing the available sub-commands. The actual work is done by the leaf commands, which derive from
 * [BaseDirectHelmCommand] or [BaseRenderedHelmCommand].
 */
abstract class BaseGroupCommand : Runnable {
    @Spec
    private lateinit var spec: CommandSpec

    override fun run() {
        spec.commandLine().usage(spec.commandLine().out)
    }
}
