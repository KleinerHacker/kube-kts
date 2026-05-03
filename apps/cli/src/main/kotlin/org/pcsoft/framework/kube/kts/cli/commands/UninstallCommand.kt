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
 * Command to uninstall a KTS-based chart repository using Helm.
 *
 * This command extends [BaseHelmCommand] and is annotated with the `@Command` annotation,
 * specifying its name as "uninstall" and providing a description indicating its purpose.
 */
@Command(name = "uninstall", description = ["Uninstall a KTS based chart repository with helm"])
class UninstallCommand : BaseHelmCommand() {
    override val helmArguments: Array<String>
        get() = arrayOf("uninstall") //TODO
}