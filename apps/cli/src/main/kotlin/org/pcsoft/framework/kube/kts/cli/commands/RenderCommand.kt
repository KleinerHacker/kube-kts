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
 * Command for rendering a Kubernetes Kotlin Script (KTS) based chart repository into YAML format.
 *
 * This command processes KTS files, compiles them into a Helm repository structure,
 * and outputs the result as YAML files to either a specified directory or a temporary location.
 */
@Command(name = "render", description = ["Render a KTS based chart repository to YAML"])
class RenderCommand : BaseRenderCommand()