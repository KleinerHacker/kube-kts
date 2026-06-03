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
 * Command for installing a KTS-based chart repository using Helm.
 *
 * This command extends [BaseHelmCommand] to execute the `helm install` operation,
 * which deploys a chart into a Kubernetes cluster. The specific arguments for the
 * Helm CLI are defined in the [helmArguments] property.
 */
@Command(name = "install", description = ["Install a KTS based chart repository with helm"])
class InstallCommand : BaseHelmCommand() {
    override val helmArguments: Array<String>
        get() = arrayOf("install") //TODO
}