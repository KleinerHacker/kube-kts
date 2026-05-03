/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
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
 * Command for linting a KTS-based Helm chart repository.
 *
 * This command executes the `helm lint` operation on the current directory,
 * validating the Helm charts for potential issues and best practice violations.
 */
@Command(name = "lint", description = ["Linting a KTS based chart repository with helm"])
class LintCommand : BaseHelmCommand() {
    override val helmArguments: Array<String>
        get() = arrayOf("lint", ".")
}