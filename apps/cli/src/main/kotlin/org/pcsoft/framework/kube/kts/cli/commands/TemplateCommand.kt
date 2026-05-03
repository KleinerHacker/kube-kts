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
import picocli.CommandLine.Option

/**
 * Command to generate a template for a KTS-based Helm chart repository.
 *
 * This command executes the `helm template` subcommand, which renders the charts
 * locally and displays them without sending any data to a cluster. The generated
 * templates can be used for testing or documentation purposes.
 */
@Command(name = "template", description = ["Print template of a KTS based chart repository with helm"])
class TemplateCommand : BaseHelmCommand() {
    @Option(names = ["-n", "--name"], description = ["Name of chart"], required = true)
    private lateinit var name: String

    override val helmArguments: Array<String>
        get() = arrayOf("template", name, ".")
}